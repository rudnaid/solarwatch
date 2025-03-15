import {Link} from "react-router-dom";
import React from "react";

const Navbar = () => {
  return (
    <nav className="navbar">

      <Link className="btn btn-ghost txt-xl" to="/register">
        Register
      </Link>

      <Link className="btn btn-ghost txt-xl" to="/login">
        Login
      </Link>

    </nav>
  )
}

export default Navbar;