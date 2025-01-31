import React from "react";
import { Link } from "react-router";
import "./Navbar.css";
import useAuth from "../hooks/useAuth";

const Navbar = () => {
  const { user } = useAuth();
  return (
    <nav className="navbar">
      <ul>
        <li>
          <Link to="/">Home</Link>
        </li>
        {!user && (
          <>
            <li>
              <Link to="/login">Login</Link>
            </li>
            <li>
              <Link to="/register">Register</Link>
            </li>
          </>
        )}
        {user && (
          <>
            <li>
              <Link to="/channels/create">Create Channel</Link>
            </li>
            <li>
              <Link to="/channels/create">Add Friend</Link>
            </li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;
