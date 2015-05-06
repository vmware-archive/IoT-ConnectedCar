class ConnectedCarController < ApplicationController
  def show
    puts latitude
    render json: {
      engine_rpm: "#{engine_rpm}",
      vehicle_speed: "#{vehicle_speed}",
      latitude: "#{latitude}",
      longitude: "#{longitude}",
      coolant_temp: coolant_temp,
      fuel_level_input: fuel_level_input,
      mpg_instantaneous: mpg,
      predictions: predictions
    }.to_json
  end

  def journeys
    render json: {
      journeys: [
        {id: "1", name: "Joe's Crab", lat: "52.497651999054305", long: "13.305280711501837"},
        {id: "2", name: "Rossi's", lat: "52.51351171173155", long: "13.293071212247014"},
        {id: "3", name: "Top Golf", lat: "52.57956363709335", long: "13.28684441575369"},
        {id: "4", name: "Welfare", lat: "52.53331150298938", long: "13.281844017803669"},
        {id: "5", name: "DFW", lat: "52.54703992512077", long: "13.405320905148985"}
      ]
    }.to_json
  end

  private

  def engine_rpm
    2500 + (rand*1000-500).to_i
  end

  def latitude
    52.54 + (rand*0.02-0.01)
  end

  def longitude
    13.34 + (rand*0.02-0.01)
  end

  def vehicle_speed
    90 + (rand*10-5).to_i
  end

  def coolant_temp
    50 + (rand*20-10).to_i
  end

  def fuel_level_input
    60 + (rand*20-10).to_i
  end

  def mpg
    30 + (rand*10-5)
  end

  def predictions
    {
      RemainingRange: 453.97174820960976 + (rand*100-50),
      ClusterPredictions: {
        "1" => {
          JourneyId: 1,
          EndLocation: [52.497651999054305, 13.305280711501837],
          Probability: 0.91198000000000001 + (rand*0.08)
        },
        "2" => {
          JourneyId: 2,
          EndLocation: [52.51351171173155, 13.293071212247014],
          Probability: 0.497800000000000005 + (rand*0.8-0.4)
        },
        "3" => {
          JourneyId: 3,
          EndLocation: [52.57956363709335, 13.28684441575369],
          Probability: 0.20
        },
        "4" => {
          JourneyId: 4,
          EndLocation: [52.53331150298938, 13.281844017803669],
          Probability: 0.0
        },
        "5" => {
          JourneyId: 5,
          EndLocation: [52.54703992512077, 13.405320905148985],
          Probability: 0.0
        }
      }
    }
  end
end
