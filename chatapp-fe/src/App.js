import { Route, Routes } from "react-router";
import "./App.css";
import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";
import Dashboard from "./pages/dashboard/Dashboard";
import Home from "./pages/home/Home";
import Layout from "./components/Layout";
import Channel from "./pages/channel/Channel";
import CreateChannel from "./pages/channel/create/CreateChannel";
import UserSearch from "./pages/users/UserSearch";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="/channels">
          <Route path=":channelId" index element={<Channel />} />
          <Route path="create" element={<CreateChannel />} />
        </Route>
        <Route path="/users" element={<UserSearch />} />
      </Route>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
    </Routes>
  );
}

export default App;
