import React, { useState } from 'react';
import axios from 'axios';
import './UserSearch.css';
import useAuth from '../../hooks/useAuth';

const UserSearch = () => {
  const { user } = useAuth();
  
  const [searchTerm, setSearchTerm] = useState('');
  const [users, setUsers] = useState([]);

  const handleSearch = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/users?search=${searchTerm}`);
      if(response.data.data.length > 0) {
        setUsers(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching users:', error);
      setUsers([]);
    }
  };

  const handleAddFriend = async (friendId) => {
    try {
      await axios.post(`http://localhost:8080/api/users/add?currentUserId=${user?.id}&friendId=${friendId}`);
      alert('Friend added successfully!');
    } catch (error) {
      console.error('Error adding friend:', error);
    }
  };

  return (
    <div className="user-search">
      <h2>Find and Add Friends</h2>
      <input
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder="Search for users..."
      />
      <button onClick={handleSearch}>Search</button>
      <ul>
        {users?.map((user) => (
          <li key={user.id}>
            <div className="user-info">
              <span>{user.name}</span>
              <span>{user.email}</span>
            </div>
            <button onClick={() => handleAddFriend(user.id)}>Add Friend</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default UserSearch;