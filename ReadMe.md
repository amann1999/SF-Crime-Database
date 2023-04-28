# DATABASE PROJECT SAN-FRANSISCO POLICE DISTRICT CRIME DATA

## How to Run

First run the sql script (projectIncidents.sql) to populate the data, which will take a few seconds. After the data is populated, do 'make run'

**For our Project, you can select the query you want to run and either dispay the output in a new window (using 'Proceed') or print out the output which will put the output in a output text file.**

**Please make sure to select a query before you click 'Proceed' or 'Download' for the output to display (it might not display if you do not select a query).**

**We are submitting the auth.cfg empty, so please make sure to add your username and password before running the code. Populating the data will not take that long since we do not have a big database.**

# DESCRIPTION

The data is modeled with records of all the incidents reported to San Francisco’s police districts as well as records of the different police districts and courts of the city, and people who are working as officers and judges or are just suspects related to an incident. Our main source for this data is taken from San Francisco’s government data, Police Department Incident Report from 2018 to the Present. The original data includes the details of all the incidents reported to SF’s police district (2018 - present), with 26 columns and 665,000 rows. However, we extracted only a small portion of this data (1000 rows) to use in our own fictional model. After preparing, and normalizing our dataset in part 1, we ended up with 15 tables, However, during the process of converting our data model to a relational database, we removed the table for suspects, resulting in 14 tables. The reason we chose this data model is that we found the original source of our data quite interesting because we are all fans of Brooklyn 99, and we also wanted to create a database where we could analyze real-world crime data and conclude meaningful results
