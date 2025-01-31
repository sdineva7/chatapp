import React from 'react'
import useAuth from '../../hooks/useAuth';

const Dashboard = () => {
    const { userChannels} = useAuth();
    
  return (
    <div>
        dashboard
    </div>
  )
}

export default Dashboard;