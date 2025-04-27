import {useState} from "react";
import {getSolarTimes} from "../../Service/apiService.js";
import {useAuth} from "../../Context/AuthContext.jsx";
import SolarWatchContent from "../../Components/Content/SolarWatchContent.jsx";
import LoadingComponent from "../../Components/Loading/LoadingComponent.jsx";
import SearchBar from "../../Components/SearchBar/SearchBar.jsx";

export const SolarWatch = () => {
  const {user} = useAuth();
  const [loading, setLoading] = useState(false);
  const [searchCompleted, setSearchCompleted] = useState(false);
  const [solarData, setSolarData] = useState({
    city: 'city',
    country: 'country',
    sunrise: 'sunrise',
    sunset: 'sunset'
  });

  const handleSearch = async (city, date) => {
    try {
      setLoading(true);
      setSearchCompleted(false);

      const result = await getSolarTimes(user, city, date);

      setSolarData({
        city: result.name,
        country: result.country,
        sunrise: result.sunriseSunset.sunrise,
        sunset: result.sunriseSunset.sunset,
        ...result
      });

      setSearchCompleted(true);
      setLoading(false);
    } catch (error) {
      console.error(error.message);
      setLoading(false);
      setSearchCompleted(false);
    }
  };

  if (loading) return (
    <div className="flex items-center justify-center h-screen">
      <LoadingComponent />
    </div>
  );

  return (
    <div className="container mx-auto px-4 py-8 bg-base-100">

      <div className="mb-8">
        <SolarWatchContent
          searchCompleted={searchCompleted}
          solarData={solarData}
        />
      </div>

      <div className="max-w-lg mx-auto mb-8">
        <SearchBar onSearch={handleSearch}/>
      </div>

    </div>
  );
};
