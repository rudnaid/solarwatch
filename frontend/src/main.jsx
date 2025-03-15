import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {createBrowserRouter, RouterProvider} from 'react-router-dom'
import Layout from './Pages/Layout/Layout.jsx'
import Register from './Pages/Register/Register.jsx'
import Login from './Pages/Login/Login.jsx'
import './index.css'
import {AuthProvider} from "./Context/AuthContext.jsx";
import {SolarWatch} from "./Pages/SolarWatch/SolarWatch.jsx";
import ProtectedRoute from "./Components/ProtectedRoute.jsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout/>,
    children: [
      {
        path: "/register",
        element: <Register/>
      },
      {
        path: "/login",
        element: <Login/>
      },
      {
        path: "/home",
        element: <ProtectedRoute><SolarWatch/></ProtectedRoute>
      }
    ]
  }
])

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router}/>
    </AuthProvider>
  </StrictMode>,
)
