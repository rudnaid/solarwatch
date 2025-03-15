import { useState } from "react"
import { registerUser } from "../../Service/apiService.js";

const RegistrationForm = ({ user, onCancel }) => {
  const [username, setUsername] = useState(user?.username || '');
  const [password, setPassword] = useState(user?.password || '');
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const onSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    if (!username || !password) {
      setErrorMessage("Username and password cannot be empty.");
      return;
    }

    try {
      const response = await registerUser({ username, password });

      if (response && response.status === 201) {
        setSuccessMessage("Registration successful!");
        setUsername('');
        setPassword('');
      } else {
        setErrorMessage("Registration failed. Please try again.");
      }

    } catch (error) {
      setErrorMessage("An error occurred during registration. Please try again.");
      console.error(error);
    }
  };

  return (
    <form className="registration-form" onSubmit={onSubmit}>
      {errorMessage && <div className="error-message">{errorMessage}</div>}
      {successMessage && <div className="success-message">{successMessage}</div>}
      <div className="control">
        <label htmlFor="username">Username:</label>
        <input
          id="username"
          name="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
      </div>
      <div className="control">
        <label htmlFor="password">Password:</label>
        <input
          id="password"
          name="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <div className="buttons">
        <button type="submit">Register</button>
        <button type="button" onClick={onCancel}>Cancel</button>
      </div>
    </form>
  );
};

export default RegistrationForm;