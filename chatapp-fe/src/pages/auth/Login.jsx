import React, { useState } from "react";
import { Link, useNavigate } from "react-router";
import "./Login.css";
import { login } from "../../services/auth/login";
import useAuth from "../../hooks/useAuth";
import { toast } from "react-toastify";

const Login = () => {
  const { login: loginUser } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  async function handleFormSubmit(e) {
    e.preventDefault();

    login(email, password)
      .then((user) => {
        if (!user) {
          toast.error("Invalid email or password");
          return;
        }
        loginUser(user).then(() =>{
            toast.success("Login successful");
            navigate("/dashboard");
        });
      })
      .catch(() => toast.error("Something went wrong"))
  }

  return (
    <div className="login-container">
      <h1>Login</h1>
      <form className="login-form" onSubmit={handleFormSubmit}>
        <label htmlFor="email">Email:</label>
        <input
          type="email"
          id="email"
          name="email"
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <label htmlFor="password">Password:</label>
        <input
          type="password"
          id="password"
          name="password"
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button type="submit">Login</button>
      </form>
      <Link to="/register">Create an account?</Link>
    </div>
  );
};

export default Login;
