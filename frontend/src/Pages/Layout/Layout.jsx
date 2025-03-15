import React, { useEffect, useRef, useState } from 'react';
import {Outlet} from "react-router-dom";
import Navbar from "../../Components/Navbar/Navbar.jsx";

const Layout = () => {
  return (
    <div>
      <Navbar />
      <Outlet />
    </div>
  );
}

export default Layout;