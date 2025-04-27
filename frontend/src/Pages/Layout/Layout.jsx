import { Outlet, useLocation } from "react-router-dom";
import Navbar from "../../Components/Navbar/Navbar.jsx";
import Footer from "../../Components/Footer/Footer.jsx";

const Layout = () => {
  const location = useLocation();

  const renderWelcomeContent = () => {
    if (location.pathname === '/') {
      return (
        <div className="flex flex-col pt-12 items-center">
          <h1>Welcome to SolarWatch!</h1>
          <p>Log in or register to search for sunrise and sunset times of a city of your choice!</p>
        </div>
      );
    }
    return null;
  };

  return (
    <div className="flex flex-col min-h-screen">

      <Navbar />

      <main className="flex-grow">
        {renderWelcomeContent()}
        <Outlet />
      </main>

      <Footer />

    </div>
  );
};

export default Layout;
