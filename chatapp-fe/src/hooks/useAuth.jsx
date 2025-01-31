import { useState, useEffect } from "react";
import { getUserChannels } from "../services/channels/getUserChannels";

const useAuth = () => {
  const [user, setUser] = useState(null);
  const [userChannels, setUserChannels] = useState(null);

  const login = async (user) => {
    try {
      const userChannels = await getUserChannels(user.id);
      localStorage.setItem("userChannels", JSON.stringify(userChannels));
      localStorage.setItem("user", JSON.stringify(user));
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem("user");
    localStorage.removeItem("userChannels");
  };

  useEffect(() => {
    if (localStorage.getItem("user")) {
      setUser(JSON.parse(localStorage.getItem("user")));
    } else {
      setUser(null);
    }
  }, [localStorage.getItem("user")]);

  useEffect(() => {
    if (user) {
      getUserChannels(user.id).then((channels) => {
        setUserChannels(channels);
      });
    }
  }, [user]);

  return { user, userChannels, login, logout };
};

export default useAuth;
