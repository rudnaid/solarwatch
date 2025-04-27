import {useState} from "react";
import SearchInputField from "../InputFields/SearchInputField.jsx";
import DateInputField from "../InputFields/DateInputField.jsx";

const SearchBar = ({onSearch}) => {
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
    <form className="flex flex-col items-center space-y-4" onSubmit={handleSubmit}>

      <SearchInputField
        placeholder={"Enter a city"}
        value={city}
        onChange={handleInputChange}
      />

      <DateInputField
        value={date}
        onChange={handleDateChange}
      />

      <button className="btn btn-soft btn-success" type="submit">Go</button>

    </form>
  );
}

export default SearchBar;
