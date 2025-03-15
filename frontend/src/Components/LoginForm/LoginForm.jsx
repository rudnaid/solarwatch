import React from "react";
import {useState} from "react";
import {loginUser} from "../../Service/apiService.js";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../../Context/AuthContext.jsx";

const LoginForm = ({onCancel}) => {
  const {login} = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const userData = await loginUser({username, password});
      login(userData);
      navigate("/home");
    } catch (error) {
      console.log(error);
      navigate("/login");
      alert("Failed to login, incorrect credentials.");
    }

  }

  return (
    <form className="login-form" onSubmit={handleLogin}>
      <div className="control">
        <label htmlFor="username">Username:</label>
        <input
          type="text"
          placeholder="Enter username"
          className="input"
          id="username"
          name="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <label htmlFor="password">Password:</label>
        <input
          type="text"
          placeholder="Enter password"
          className="input"
          id="password"
          name="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <div className="buttons">
        <button type="submit">
          Login
        </button>
        <button type="button" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  )
}

export default LoginForm;
