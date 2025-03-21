import { Link } from "react-router-dom";
import './Header.styles.css';
import { useEffect, useState } from "react";

const Header = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Check token validity on component mount
    validateToken();
  }, []);
  
  const validateToken = () => {
    const token = localStorage.getItem("token");
    if (token == null) {
      setIsAuthenticated(false);
      return;
    }
    
    try {
      // For JWT tokens
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiration = payload.exp * 1000; // Convert to milliseconds
      
      if (Date.now() >= expiration) {
        // Token has expired
        localStorage.removeItem("token"); // Optional: remove expired token
        setIsAuthenticated(false);
      } else {
        setIsAuthenticated(true);
      }
    } catch (error) {
      console.error("Invalid token format", error);
      localStorage.removeItem("token"); // Optional: remove expired token
      setIsAuthenticated(false);
    }
  };

  return (
    <div className='header_container'>
      <div className="header_bar">
        <span>
          <Link to="/" className="nav-link">
            Price Tracker
          </Link>
        </span>
        {!isAuthenticated && (
          <>
            <span>
              <Link to="/login" className="nav-link">
                Login
              </Link>
            </span>
            <span>
              <Link to="/register" className="nav-link">
                Sign Up
              </Link>
            </span>
          </>
        )}
        {isAuthenticated && (
          <span>
            <Link to="/" className="nav-link" onClick={() => {
              localStorage.removeItem("token");
              setIsAuthenticated(false);
            }}>
              Logout
            </Link>
          </span>
        )}
      </div>
      <div className='header_middle'></div>
      <div className='header_bottom'></div>
    </div>
  );
};

export default Header;