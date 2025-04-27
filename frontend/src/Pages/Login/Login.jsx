import React from "react";
import LoginForm from "../../Components/Forms/LoginForm.jsx";
import {useNavigate} from "react-router-dom";

const Login = () => {
  const navigate = useNavigate();

  const handleCancel = () => {
    navigate("/");
  }

  return (
    <div className="flex justify-center pt-12 min-h-screen">
      <LoginForm onCancel={handleCancel} />
    </div>
  )
}

export default Login;
