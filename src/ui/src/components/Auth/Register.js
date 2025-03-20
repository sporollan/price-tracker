// src/components/Auth/Register.js
import axios from 'axios';
import { useState } from "react";
import { useNavigate } from "react-router-dom";
axios.defaults.baseURL = 'http://ui.local';

const Register = () => {
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const response = await axios.post('user',{
        username: username,
        password: password,
        email: email
    },{
        headers: { 'Content-Type': 'application/json' } // Optional (Axios defaults to JSON)
      });
      alert("Registration successful! Please log in.");
      navigate("/login");
    } catch (error) {
      if (error.response) {
        // Server responded with a status outside 2xx
        alert(`Registration failed: ${error.response.data}`);
      } else if (error.request) {
        // No response was received
        alert("No response from server. Please try again.");
      } else {
        // Other errors
        alert("Registration failed. Please try again.");
      }
      console.error(error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input 
        type="email" 
        placeholder="Email" 
        value={email} 
        onChange={(e) => setEmail(e.target.value)} 
        required 
      />
      <input 
        type="text" 
        placeholder="Username" 
        value={username} 
        onChange={(e) => setUsername(e.target.value)} 
        required 
      />
      <input 
        type="password" 
        placeholder="Password" 
        value={password} 
        onChange={(e) => setPassword(e.target.value)} 
        required 
      />
      <button type="submit">Register</button>
    </form>
  );
};

export default Register;