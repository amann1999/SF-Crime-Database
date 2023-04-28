
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.awt.*;
import java.io.PrintWriter;
import javax.swing.*;


public class SQLServerDemo {

    // Connect to your database.
    // Replace server name, username, and password with your credentials
    public static void main(String[] args) {

        Properties prop = new Properties();
        String fileName = "auth.cfg";
        try {
            FileInputStream configFile = new FileInputStream(fileName);
            prop.load(configFile);
            configFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find config file.");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error reading config file.");
            System.exit(1);
        }
        String username = (prop.getProperty("username"));
        String password = (prop.getProperty("password"));

        if (username == null || password == null) {
            System.out.println("Username or password not provided.");
            System.exit(1);
        }

        String connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                + "database=cs3380;"
                + "user=" + username + ";"
                + "password=" + password + ";"
                + "encrypt=false;"
                + "trustServerCertificate=false;"
                + "loginTimeout=30;";

        ResultSet resultSet = null;

        try (Connection connection = DriverManager.getConnection(connectionUrl);
                Statement statement = connection.createStatement();) {

            String[] optionsToChoose = { "A Police report of all incidents that were commited.", "List of judges that only work in one court house. ", 
            "The list of all the most wanted criminals. ", "List of the safest neighbourhoods to visit for each time of the day.", "Did a neighbourhood get more or less violent and by what percentage?", "The avarege crime rate for each neighbourhood", 
            "List of the officers that are supects of an incident. ", "Which courts have sent the most suspects to prison?", "Number of incidents reported to each of the police district. ", "Report the people who have commited an assault but not found guilty.",
            "Report the list of the number of crimes commited in each category . " };

            JFrame jFrame = new JFrame();
            jFrame.setTitle("San Fransisco Police Crime Database"); 
            JComboBox<String> jComboBox = new JComboBox<>(optionsToChoose);
            jComboBox.setBounds(80, 50, 140, 20);

            JComboBox<String> resultOption = new JComboBox<>(new String[] { "Print", "Download" });
            resultOption.setBounds(80, 100, 140, 20);

            JButton jButton = new JButton("Proceed");
            jButton.setBounds(100, 150, 200, 20);

            JLabel result = new JLabel("");

            result.setBounds(100, 150, 700, 700);

            jFrame.add(jButton);
            jFrame.add(jComboBox);
            jFrame.add(resultOption);
            jFrame.add(result);

            jFrame.setLayout(null);
            jFrame.setSize(500, 500);
            jFrame.setVisible(true);

            // -------------------------------------------- Q1 --------------------

           
            String selectSql = "select incident_date,name,incident_category,incident_description,police_district from incident \n"
                    +
                    "inner join commited on incident.incident_key= commited.incident_key \n" +
                    "inner join people on commited.SIN = people.SIN \n" +
                    "inner join incidentDate on incident.date_id=incidentDate.date_id \n" +
                    "inner join category on incident.incident_code = category.incident_code \n" +
                    "inner join policeStation on incident.police_station_id = policeStation.police_station_id";
            resultSet = statement.executeQuery(selectSql);
            String[] q1columns = { "Date of the Incident", "Name","Incident Category", "Incident Description", "Police District" };
            String[][] q1Output = new String[4000][400];
            int colsq1 = 0;
            int rowsq1 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq1 = 0; colsq1 < q1columns.length; colsq1++) {
                    q1Output[rowsq1][colsq1] = resultSet.getString(colsq1 + 1);
                }
                rowsq1++;

            }

            final String[][] temp = new String[rowsq1][q1columns.length];
            for (int r = 0; r < rowsq1; r++) {
                for (int c = 0; c < q1columns.length; c++) {
                    temp[r][c] = q1Output[r][c];
                }
            }
            final String[][] output1 = temp;

            // -------------------------------------------- Q2

            String selectSql2 = "select staff_id,name,court_house from people \n " +
                    "inner join judge as outerJudge on people.SIN=outerJudge.[SIN] \n " +
                    "inner join employs on outerJudge.[SIN]=employs.[SIN] \n " +
                    "inner join court as outterCourt on employs.court_id=outterCourt.court_id \n " +
                    "where staff_id not in ( \n " +
                    "select staff_id  from people \n " +
                    "inner join judge as innerJudge on people.SIN=innerJudge.[SIN] \n " +
                    "inner join employs on innerJudge.[SIN]=employs.[SIN] \n " +
                    "inner join court as innerCourt on employs.court_id=innerCourt.court_id\n " +
                    "where outerJudge.staff_id =innerJudge.staff_id and outterCourt.court_id != innerCourt.court_id ) ";

            resultSet = statement.executeQuery(selectSql2);

            String[] q2columns = { "Staff ID", "Name", "Court House" };
            String[][] q2Output = new String[4000][400];
            int colsq2 = 0;
            int rowsq2 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq2 = 0; colsq2 < q2columns.length; colsq2++) {
                    q2Output[rowsq2][colsq2] = resultSet.getString(colsq2 + 1);
                }
                rowsq2++;

            }

            final String[][] temp2 = new String[rowsq2][q2columns.length];
            for (int r = 0; r < rowsq2; r++) {
                for (int c = 0; c < q2columns.length; c++) {
                    temp2[r][c] = q2Output[r][c];
                }
            }
            final String[][] output2 = temp2;

            // -------------------------------------------- Q3 --------------

            String selectSql3 = "with numberCrimes as (select  people.SIN,count(incident.incident_key) as countCrimes from incident \n"
                    +
                    "inner join commited on incident.[incident_key] =  commited.[incident_key] \n" +
                    "inner join people on commited.[SIN]=people.[SIN] \n" +
                    "group by people.[SIN] \n" +
                    "having count(incident.incident_key)  > 1) \n" +
                    "select top 5 name, countCrimes \n" +
                    "from numberCrimes \n" +
                    "join people on numberCrimes.[SIN]=people.[SIN] \n" +
                    "order by countCrimes desc";

            resultSet = statement.executeQuery(selectSql3);

            String[] q3columns = { "Name", "Number of Crimes" };
            String[][] q3Output = new String[4000][400];
            int colsq3 = 0;
            int rowsq3 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq3 = 0; colsq3 < q3columns.length; colsq3++) {
                    q3Output[rowsq3][colsq3] = resultSet.getString(colsq3 + 1);
                }
                rowsq3++;

            }

            final String[][] temp3 = new String[rowsq3][q3columns.length];
            for (int r = 0; r < rowsq3; r++) {
                for (int c = 0; c < q3columns.length; c++) {
                    temp3[r][c] = q3Output[r][c];
                }
            }
            final String[][] output3 = temp3;

            // -------------------------------------------- Q4 ------------------------
            String selectSql4 = "select neighbourhood,count(incident_key) as numCrimes, \n" +
                    "CASE \n" +
                    "when incident_time >= '05:00:00' and incident_time < '12:00:00'\n" +
                    "then 'Morning'\n" +
                    "when incident_time >= '12:00:00' and incident_time <'20:20:20' \n" +
                    "then 'Afternoon'\n" +
                    "else \n" +
                    "'Evening'\n" +
                    "end as period \n" +
                    "from incident \n" +
                    "inner join incidentDate on incident.date_id=incidentDate.date_id\n" +
                    "group by neighbourhood, CASE \n" +
                    "when incident_time >= '05:00:00' and incident_time < '12:00:00'\n" +
                    "then 'Morning'\n" +
                    "when incident_time >= '12:00:00' and incident_time <'20:20:20' \n" +
                    "then 'Afternoon'\n" +
                    "else \n" +
                    "'Evening'\n" +
                    "end\n" +
                    "having count(incident_key)= 1\n" +
                    "order by period,numCrimes asc\n";

            resultSet = statement.executeQuery(selectSql4);

            String[] q4columns = { "Neighbourhood", "Number of Crimes","Period" };
            String[][] q4Output = new String[4000][400];
            int colsq4 = 0;
            int rowsq4 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq4 = 0; colsq4 < q4columns.length; colsq4++) {
                    q4Output[rowsq4][colsq4] = resultSet.getString(colsq4 + 1);
                }
                rowsq4++;

            }

            final String[][] temp4 = new String[rowsq4][q4columns.length];
            for (int r = 0; r < rowsq4; r++) {
                for (int c = 0; c < q4columns.length; c++) {
                    temp4[r][c] = q4Output[r][c];
                }
            }
            final String[][] output4 = temp4;

            // // -------------------------------------------- Q5 ----------------

            String selectSql5 = "with janMonth as(select neighbourhood,count(incident.incident_key) as counterCrimesJanuary, MONTH(incident_date) as month from incident "+
            "inner join incidentDate on incident.date_id=incidentDate.date_id "+
            "inner  join commited on incident.incident_key=commited.incident_key "+
            "group by MONTH(incident_date),neighbourhood "+
            "having MONTH(incident_date) = 1), "+
            "augMonth as (select neighbourhood,count(incident.incident_key) as counterCrimesAugust, MONTH(incident_date) as month from incident "+
            "inner join incidentDate on incident.date_id=incidentDate.date_id "+
            "inner  join commited on incident.incident_key=commited.incident_key "+
            "group by MONTH(incident_date),neighbourhood "+
            "having MONTH(incident_date) = 8) "+
            "select augMonth.neighbourhood,counterCrimesJanuary, counterCrimesAugust, "+
            "CASE "+
            "when counterCrimesJanuary - counterCrimesAugust >= 0 "+
            "then 'Less' " +
            "else "+
            "'More' "+
            "end as period , concat(round((cast(counterCrimesJanuary as float)/(counterCrimesJanuary + counterCrimesAugust) "+
            " -  cast(counterCrimesAugust as float)/(counterCrimesJanuary + counterCrimesAugust))*100,2),'%') as HowManyPercentage "+
            "from janMonth "+
            "inner join augMonth on augMonth.neighbourhood=janMonth.neighbourhood ";
            resultSet = statement.executeQuery(selectSql5);

            String[] q5columns = { "Neighbouhood", "Count Crimes in January", "Crimes count in August", "Period",
                    "How Much the Percentage" };
            String[][] q5Output = new String[4000][400];
            int colsq5 = 0;
            int rowsq5 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq5 = 0; colsq5 < q5columns.length; colsq5++) {
                    q5Output[rowsq5][colsq5] = resultSet.getString(colsq5 + 1);
                }
                rowsq5++;

            }

            final String[][] temp5 = new String[rowsq5][q5columns.length];
            for (int r = 0; r < rowsq5; r++) {
                for (int c = 0; c < q5columns.length; c++) {
                    temp5[r][c] = q5Output[r][c];
                }
            }
            final String[][] output5 = temp5;

            // -------------------------------------------- Q6 -----------------

            String selectSql6 = " with TotalCrime as (select count(*) as TotalCrime from incident \n" +
                    "inner join commited on incident.incident_key=commited.incident_key), \n" +
                    "CountNeighbourhood as( \n" +
                    "select count(incident.incident_key) as countPlace,neighbourhood from incident \n" +
                    "inner join commited commited on incident.incident_key=commited.incident_key \n" +
                    "group by neighbourhood)  \n" +
                    "select neighbourhood,concat(round(cast(countPlace as float)/cast(TotalCrime as float) * 100,2), '%') as CrimeRate from TotalCrime,CountNeighbourhood where neighbourhood is not null";

            resultSet = statement.executeQuery(selectSql6);

            String[] q6columns = { "Neighbouhood", "Crime Rate" };
            String[][] q6Output = new String[4000][400];
            int colsq6 = 0;
            int rowsq6 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq6 = 0; colsq6 < q6columns.length; colsq6++) {
                    q6Output[rowsq6][colsq6] = resultSet.getString(colsq6 + 1);
                }
                rowsq6++;

            }

            final String[][] temp6 = new String[rowsq6][q6columns.length];
            for (int r = 0; r < rowsq6; r++) {
                for (int c = 0; c < q6columns.length; c++) {
                    temp6[r][c] = q6Output[r][c];
                }
            }
            final String[][] output6 = temp6;

            // -------------------------------------------- Q7 ---------------------

            String selectSql7 = "select name, rank,police_district from people \n" +
                    "join officer on people.[SIN]=officer.[SIN]\n" +
                    "join hires on people.[SIN]=hires.[SIN]\n" +
                    "join policeStation on hires.police_station_id=policeStation.police_station_id\n" +
                    "where employee_number is not null and suspect_id is not null";

            resultSet = statement.executeQuery(selectSql7);

            String[] q7columns = { "Name", "Rank", "Police District" };
            String[][] q7Output = new String[4000][400];
            int colsq7 = 0;
            int rowsq7 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq7 = 0; colsq7 < q7columns.length; colsq7++) {
                    q7Output[rowsq7][colsq7] = resultSet.getString(colsq7 + 1);
                }
                rowsq7++;

            }

            final String[][] temp7 = new String[rowsq7][q7columns.length];
            for (int r = 0; r < rowsq7; r++) {
                for (int c = 0; c < q7columns.length; c++) {
                    temp7[r][c] = q7Output[r][c];
                }
            }
            final String[][] output7 = temp7;

            // -------------------------------------------- Q8 ---------------------------

            String selectSql8 = "select court_house, count(incident_key) as NumberOfPrisonSentences from court \n" +
                    "inner join deals on court.court_id=deals.court_id \n" +
                    "where sentence = 'Prison' \n" +
                    "group by court.court_id,court_house";

            resultSet = statement.executeQuery(selectSql8);

            String[] q8columns = { "Court House", "Number of Prison Sentences" };
            String[][] q8Output = new String[4000][400];
            int colsq8 = 0;
            int rowsq8 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq8 = 0; colsq8 < q8columns.length; colsq8++) {
                    q8Output[rowsq8][colsq8] = resultSet.getString(colsq8 + 1);
                }
                rowsq8++;

            }

            final String[][] temp8 = new String[rowsq8][q8columns.length];
            for (int r = 0; r < rowsq8; r++) {
                for (int c = 0; c < q8columns.length; c++) {
                    temp8[r][c] = q8Output[r][c];
                }
            }
            final String[][] output8 = temp8;

            // -------------------------------------------- Q9 ---------------------------

            String selectSql9 = "select police_district, count(incident_key) as numIncidents from policeStation\n" +
            "inner join incident on policeStation.police_station_id=incident.police_station_id \n" +
            "group by policeStation.police_station_id,police_district";

            resultSet = statement.executeQuery(selectSql9);

            String[] q9columns = { "Police District", "Number of Incidents" };
            String[][] q9Output = new String[4000][400];
            int colsq9 = 0;
            int rowsq9 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq9 = 0; colsq9 < q9columns.length; colsq9++) {
                    q9Output[rowsq9][colsq9] = resultSet.getString(colsq9 + 1);
                }
                rowsq9++;

            }

            final String[][] temp9 = new String[rowsq9][q9columns.length];
            for (int r = 0; r < rowsq9; r++) {
                for (int c = 0; c < q9columns.length; c++) {
                    temp9[r][c] = q9Output[r][c];
                }
            }
            final String[][] output9 = temp9;

            // -------------------------------------------- Q10 ---------------------------

            String selectSql10 = "select distinct name,sentence,incident_description from people \n" +
            "inner join commited on people.[SIN]=commited.[SIN] \n" +
            "inner join incident on commited.incident_key=incident.incident_key \n" +
            "inner join category on incident.incident_code=category.incident_code \n" +
            "inner join deals on incident.incident_key=deals.incident_key \n" +
            "where incident_category = 'Assault' and sentence='Found Innocent'";

            resultSet = statement.executeQuery(selectSql10);

            String[] q10columns = { "Name", "Sentence","Incident Description" };
            String[][] q10Output = new String[4000][400];
            int colsq10 = 0;
            int rowsq10 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq10 = 0; colsq10 < q10columns.length; colsq10++) {
                    q10Output[rowsq10][colsq10] = resultSet.getString(colsq10 + 1);
                }
                rowsq10++;

            }

            final String[][] temp10 = new String[rowsq10][q10columns.length];
            for (int r = 0; r < rowsq10; r++) {
                for (int c = 0; c < q10columns.length; c++) {
                    temp10[r][c] = q10Output[r][c];
                }
            }
            final String[][] output10 = temp10;

            // -------------------------------------------- Q11 ---------------------------
            // includes 0's

            String selectSql11 = "with allCrimes as (select distinct people.[SIN],incident.incident_key from people \n" +
            "inner join commited on people.SIN=commited.[SIN] \n" +
            "inner join incident on commited.incident_key=incident.incident_key \n" +
            "inner join category on incident.incident_code=category.incident_code), \n" +
            "allIncidentes as ( select incident.incident_key, count(allCrimes.[SIN]) as countCrimes from incident \n" +
                "left join allCrimes on incident.incident_key= allCrimes.incident_key \n" +
                "group by incident.incident_key)  \n" + 
            "select category.incident_category,allIncidentes.countCrimes from allIncidentes \n" +    
            "inner join incident on allIncidentes.incident_key=incident.incident_key \n" +
            "inner join category on incident.incident_code=category.incident_code";

            resultSet = statement.executeQuery(selectSql11);

            String[] q11columns = { "Incident Category", "Crimes Count" };
            String[][] q11Output = new String[4000][400];
            int colsq11 = 0;
            int rowsq11 = 0;
            // Print results from select statement
            while (resultSet.next()) {
                for (colsq11 = 0; colsq11 < q11columns.length; colsq11++) {
                    q11Output[rowsq11][colsq11] = resultSet.getString(colsq11 + 1);
                }
                rowsq11++;

            }

            final String[][] temp11 = new String[rowsq11][q11columns.length];
            for (int r = 0; r < rowsq11; r++) {
                for (int c = 0; c < q11columns.length; c++) {
                    temp11[r][c] = q11Output[r][c];
                }
            }
            final String[][] output11 = temp11;


            // -------------------------- INTERFACE ---------------------

            jButton.addActionListener(e -> {
                String[] columnNames = null;
                String[][] output = new String[4000][4000];
                if (jComboBox.getSelectedIndex() == 0) { // query 1
                    output = (output1);
                    columnNames = q1columns;

                } else if (jComboBox.getSelectedIndex() == 1) { // Query 2
                    output = (output2);
                    columnNames = q2columns;
                } else if (jComboBox.getSelectedIndex() == 2) { // Query 3
                    output = (output3);
                    columnNames = q3columns;
                } else if (jComboBox.getSelectedIndex() == 3) { // Query 4
                    output = (output4);
                    columnNames = q4columns;
                } else if (jComboBox.getSelectedIndex() == 4) { // Query 5
                    output = (output5);
                    columnNames = q5columns;
                } else if (jComboBox.getSelectedIndex() == 5) { // Query 6
                    output = (output6);
                    columnNames = q6columns;
                } else if (jComboBox.getSelectedIndex() == 6) { // Query 7
                    output = (output7);
                    columnNames = q7columns;
                } else if (jComboBox.getSelectedIndex() == 7) { // Query 8
                    output = (output8);
                    columnNames = q8columns;
                } else if (jComboBox.getSelectedIndex() == 8) { // Query 9
                    output = (output9);
                    columnNames = q9columns;
                } else if (jComboBox.getSelectedIndex() == 9) { // Query 10
                    output = (output10);
                    columnNames = q10columns;
                }else if (jComboBox.getSelectedIndex() == 10) { // Query 11
                    output = (output11);
                    columnNames = q11columns;
                 }

                if (resultOption.getSelectedIndex() == 0) {
                   
                    // crating the new screen
                    final JFrame frame = new JFrame("Query Output");
                    final JTable j;

                    // Display the window.
                    frame.setSize(1000, 800);
                    frame.setVisible(true);

                    // set flow layout for the frame
                    frame.getContentPane().setLayout(new FlowLayout());

                    // Initializing the JTable
                    j = new JTable(output, columnNames);
                    //j.setBounds(0, 0, 1000, 1000);
                    for(int i=0;i<columnNames.length;i++){
                        if(columnNames[i].equals("Incident Description")){
                            j.getColumnModel().getColumn(i).setPreferredWidth(400);
                        }
                        else if(columnNames[i].equals("Neighbouhood")){
                            j.getColumnModel().getColumn(i).setPreferredWidth(200);
                        }
                        else if(columnNames[i].equals("Incident Category")){
                            j.getColumnModel().getColumn(i).setPreferredWidth(260);
                        }
                        else{
                            j.getColumnModel().getColumn(i).setPreferredWidth(130);
                        }
                        
                    }

                    // adding it to JScrollPane
                    JScrollPane sp = new JScrollPane(j,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    frame.add(sp);
                    j.setAutoscrolls(true);
                    j.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                    // Frame Size
                    frame.setSize(1000, 1000);
                    // Frame Visible = true
                    frame.setVisible(true);

                } else {
                    try {
                        PrintWriter outputF = new PrintWriter("output.txt");
                        outputF.println(output);
                        outputF.close();
                    } catch (Exception fileError) {

                    }

                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
