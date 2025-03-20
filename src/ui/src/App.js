import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Auth/Login";
import Register from "./components/Auth/Register";
import ProtectedRoute from "./components/Auth/ProtectedRoute";
import Header from "./components/Header/Header"
import Home from "./components/Home/Home"
import { useState } from "react";

function App() {
  const [userId, setUserId] = useState("");
  return (
    <Router>
      <div className="main_container">
        <Header />
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login setUserId={setUserId}/>} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<Home />}/>
        </Routes>
      </div>
    </Router>
  );
}

export default App