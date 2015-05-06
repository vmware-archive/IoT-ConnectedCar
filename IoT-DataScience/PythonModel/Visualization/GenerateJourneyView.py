import folium
import brewer2mpl
from sklearn.externals import joblib

filename = 'Output/Models/KMeans_JourneyClusters'
journeyClusters = joblib.load(filename)

tileset = r'http://{s}.www.toolserver.org/tiles/bw-mapnik/{z}/{x}/{y}.png'
m = folium.Map(location=[52.52, 13.398], zoom_start=11, tiles=tileset, attr='OpenStreetMap Black&White')

bmap = brewer2mpl.get_map('Paired', 'qualitative', len(journeyClusters))
colors = bmap.hex_colors
color_indx = 0

print colors

for journeyCluster in journeyClusters:
    if journeyCluster.clusterID == -1:
        continue

    for journey in journeyCluster.journeys:
        #print journey.data[['Latitude','Longitude']].dropna().values
        m.polygon_marker(location=journey.data[['Latitude','Longitude']].dropna().values[0], 
        								 popup=journey.filename + 'Start',
        								 fill_color=colors[color_indx],
        								 num_sides=3,
        								 radius=10)

        m.polygon_marker(location=journey.data[['Latitude','Longitude']].dropna().values[-1], 
        								 popup=journey.filename + 'Start',
        								 fill_color=colors[color_indx],
        								 num_sides=4,
        								 radius=10)

        m.line(journey.data[['Latitude','Longitude']].dropna().values, line_color=colors[color_indx], line_weight=2)
    
    color_indx = color_indx + 1

m.create_map(path='Visualization/ViewJourneys.html')


# generate cluster view
mc = folium.Map(location=[52.52, 13.398], zoom_start=11, tiles=tileset, attr='OpenStreetMap Black&White')

bmap = brewer2mpl.get_map('Paired', 'qualitative', len(journeyClusters))
colors = bmap.hex_colors
color_indx = 0

print colors

for journeyCluster in journeyClusters:
    if journeyCluster.clusterID == -1:
        continue

    mc.polygon_marker(location=journeyCluster.averages[['StartLat','StartLong']], 
                      popup='Cluster ' + str(journeyCluster.clusterID) + ' Start',
                      fill_color=colors[color_indx],
                      num_sides=3,
                      radius=10)

    mc.polygon_marker(location=journeyCluster.averages[['EndLat','EndLong']], 
                      popup='Cluster ' + str(journeyCluster.clusterID) + ' End',
                      fill_color=colors[color_indx],
                      num_sides=4,
                      radius=10)

    mc.line([journeyCluster.averages[['StartLat','StartLong']], journeyCluster.averages[['EndLat','EndLong']]],
            line_color=colors[color_indx], 
            line_weight=5)
    

    for journey in journeyCluster.journeys:
        #print journey.data[['Latitude','Longitude']].dropna().values
        mc.polygon_marker(location=journey.data[['Latitude','Longitude']].dropna().values[0], 
                                         popup=journey.filename + ' Start',
                                         fill_color=colors[color_indx],
                                         num_sides=3,
                                         radius=5,
                                         fill_opacity=0.5)

        mc.polygon_marker(location=journey.data[['Latitude','Longitude']].dropna().values[-1], 
                                         popup=journey.filename + ' Start',
                                         fill_color=colors[color_indx],
                                         num_sides=4,
                                         radius=5,
                                         fill_opacity=0.5)

        mc.line([journey.data[['Latitude','Longitude']].dropna().values[0], journeyCluster.averages[['StartLat','StartLong']]],
            line_color=colors[color_indx], 
            line_weight=1)

        mc.line([journey.data[['Latitude','Longitude']].dropna().values[-1], journeyCluster.averages[['EndLat','EndLong']]],
            line_color=colors[color_indx], 
            line_weight=1)

    color_indx = color_indx + 1

mc.create_map(path='Visualization/ViewClusters.html')
