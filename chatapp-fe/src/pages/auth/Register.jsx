import React, { useState } from "react";
import { Link, useNavigate } from "react-router";
import "./Register.css";
import { register } from "../../services/auth/register";
import { toast } from "react-toastify";

const Register = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  async function handleFormSubmit(e) {
    e.preventDefault();

    register(email,username, password)
      .then(() => {
        toast.success("Register successful");
        navigate("/login");
      })
      .catch(() => toast.error("Something went wrong"));
  }

  return (
    <div className="register-container">
      <h1>Register</h1>
      <form className="register-form" onSubmit={handleFormSubmit}>
        <label htmlFor="email">Email:</label>
        <input type="email" id="email" name="email" onChange={(e) => setEmail(e.target.value)} required />

        <label htmlFor="username">Username:</label>
        <input type="text" id="username" name="username" onChange={(e) => setUsername(e.target.value)} required />

        <label htmlFor="password">Password:</label>
        <input type="password" id="password" name="password" onChange={(e) => setPassword(e.target.value)} required />

        <button type="submit">Register</button>
      </form>
        <Link to="/login">Already have an account?</Link>
    </div>
  );
};

export default Register;
