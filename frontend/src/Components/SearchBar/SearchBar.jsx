import {useState} from "react";

export const SearchBar = ({ onSearch }) => {
  const [city, setCity] = useState('');
  const [date, setDate] = useState('');

  const handleInputChange = (e) => {
    setCity(e.target.value);
  }

  const handleDateChange = (e) => {
    setDate(e.target.value);
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    if (city.trim()) {
      onSearch(city, date);
    }
  }

  return (
    <form className="search-bar" onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Search for a city"
        value={city}
        onChange={handleInputChange}
      />
      <input
        type="date"
        value={date}
        onChange={handleDateChange}
      />
      <button className="search-button" type="submit">GO!</button>
    </form>
  );
}