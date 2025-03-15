import axios from "axios";

export const registerUser = async (user) => {
  try {
    return await axios.post("/api/user/register", user);
  } catch (error) {
    console.error("Error:", error);
  }
}

export const loginUser = async (user) => {
  try {
    const response = await axios.post("/api/user/login", user);

    return response.data;
  } catch (error) {
    console.error("Error:", error);
  }
}

export const getSolarTimes = async (user, city, date = null, tzid = null, formatted = null) => {
  const params = {city}

  if (date) params.date = date;
  if (tzid) params.tzid = tzid;
  if (formatted) params.formatted = formatted;

  try {
    const response = await axios.get('/api/solarwatch/times', {
      params,
      headers: {
        'Authorization': `Bearer ${user.jwtToken}`,
      }
    });

    return response.data;
  } catch (error) {
    console.error('Error fetching solar times:', error);
    throw error;
  }
};
