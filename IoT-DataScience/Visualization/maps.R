library(leaflet)
library(RColorBrewer)

data <- read.csv(file="~/Dropbox/Pivotal/IoT-ConnectedCar/data/pydata_london_csv/data.csv")

pal <- brewer.pal(6, name="RdYlGn")

factpal <- colorFactor(topo.colors(6), data$journey_id)

m1 <- data[data$journey_id == unique(data$journey_id)[1], ]
m2 <- data[data$journey_id == unique(data$journey_id)[2], ]
m3 <- data[data$journey_id == unique(data$journey_id)[3], ]
m4 <- data[data$journey_id == unique(data$journey_id)[4], ]
m5 <- data[data$journey_id == unique(data$journey_id)[5], ]
m6 <- data[data$journey_id == unique(data$journey_id)[6], ]

m <- leaflet() %>%
addProviderTiles("CartoDB.Positron") %>%
#addTiles()  %>%
addPolylines(lng=m1$longitude, lat=m1$latitude, color=pal[1]) %>%
addPolylines(lng=m2$longitude, lat=m2$latitude, color=pal[2]) %>%
addPolylines(lng=m3$longitude, lat=m3$latitude, color=pal[3]) %>%
addPolylines(lng=m4$longitude, lat=m4$latitude, color=pal[4]) %>%
addPolylines(lng=m5$longitude, lat=m5$latitude, color=pal[5]) %>%
addPolylines(lng=m6$longitude, lat=m6$latitude, color=pal[6])
m
