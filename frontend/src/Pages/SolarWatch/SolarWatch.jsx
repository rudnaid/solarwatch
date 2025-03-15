import { useState} from "react";
import {getSolarTimes} from "../../Service/apiService.js";
import {SearchBar} from "../../Components/SearchBar/SearchBar.jsx";
import {useAuth} from "../../Context/AuthContext.jsx";

export const SolarWatch = () => {
  const {user} = useAuth();
  const [city, setCity] = useState('');
  const [country, setCountry] = useState('');
  const [sunrise, setSunrise] = useState('');
  const [sunset, setSunset] = useState('');

  const handleSearch = async (city, date) => {
    try {
      const result = await getSolarTimes(user, city, date);

      setCity(result.name);
      setCountry(result.country)
      setSunrise(result.sunriseSunset.sunrise);
      setSunset(result.sunriseSunset.sunset);

    } catch (error) {
      console.error(error.message);
    }
  }

  return (
    <div>
      <div>
        <SearchBar onSearch={handleSearch} />
      </div>

      <div>
        <div>City: {city}</div>
        <div>Country: {country}</div>
        <div>Sunrise: {sunrise}</div>
        <div>Sunset: {sunset}</div>
      </div>

    </div>
  )
};