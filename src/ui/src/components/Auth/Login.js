import axios from 'axios';
import { useState } from "react";
import { useNavigate } from "react-router-dom";
axios.defaults.baseURL = 'http://ui.local';


const Login = ({setUserId}) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const response = await axios.post('login', {
        email: email,
        password: password
      }, {
        headers: { 'Content-Type': 'application/json' } // Optional (Axios defaults to JSON)
      });

      console.log(response.data)
      
      if (response.status === 200) {
        const token = response.data
        localStorage.setItem("token", token); // Store JWT
        localStorage.setItem("userId", email);
        setUserId(email);
        navigate("/");
      } else {
        console.log("Login failed");
      }
    } catch (error) {
      console.error(error);
      alert("Login failed")
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input 
        type="email" 
        placeholder="Email" 
        value={email} 
        onChange={(e) => setEmail(e.target.value)} 
      />
      <input 
        type="password" 
        placeholder="Password" 
        value={password} 
        onChange={(e) => setPassword(e.target.value)} 
      />
      <button type="submit">Login</button>
    </form>
  );
};

export default Login;