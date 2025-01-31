import React from 'react'
import { Link } from 'react-router'
import './Sidebar.css'
import useAuth from '../hooks/useAuth'

const Sidebar = () => {
  const { userChannels } = useAuth()

  console.log('userChannels', userChannels);
  
  return (
    <div className="sidebar">
      <h2>Channels</h2>
      <ul>
        {userChannels?.map((channel) => (
          <li key={channel.id}>
            <Link to={`/channels/${channel.id}`}>{channel.name}</Link>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default Sidebar