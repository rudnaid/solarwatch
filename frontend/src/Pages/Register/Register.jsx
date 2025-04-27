import React from "react";
import RegistrationForm from "../../Components/Forms/RegistrationFrom.jsx";
import {useNavigate} from "react-router-dom";

const Register = () => {
  const navigate = useNavigate();

  const handleCancel = () => {
    navigate("/");
  }

  return (
    <div className="flex justify-center pt-12 min-h-screen">
      <RegistrationForm onCancel={handleCancel} />
    </div>
  )
}

export default Register;
