Seon Challenge Submission

Invisible Protection
Our main idea was to ask for the help of people in order to collect extra data, so we could construct a widely usable database to connect IPs and locations. This way we can validate user activities and mark VPN servers.

To achieve this, we created a mobile application as well as a web server which visualizes the collected data. In the mobile application you can voluntarily share your IP address and location with a precision of your decision. You can also choose the frequency of the data sending (between 5 min and 12 hours). Aside from working against cyber crimes, in exchange for your help you will get different rewards from our partners (discount, coupons, in-game currency, etc.). Also, if you are concerned about your private VPN usage, you can turn off your location sharing and only send your network data.

After your data arrives at the webserver it gets stored in a relational database. With the help of this we visualize the connection between IPs and locations on the world map and unravel VPN servers.

To reach this goal we used the followings:

REST API for communication between the mobile app and the webserver
PostgreSQL for the database
Heroku for the webserver
SpringBoot framework for the webserver backend
OpenStreetMap API for data visualization
The link to our webserver

Our future plans include generating IDs for the rewards so people can share their coupons with others (for example, influencers). We would also like to have a community feature where you would receive more/better offers if you join one.
