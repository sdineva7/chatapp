import React from 'react'
import { useParams } from 'react-router'
import Chat from '../../components/Chat'

const Channel = () => {
    const { channelId } = useParams()
  return (
    <div>
        <Chat channelId={channelId} />
    </div>
  )
}

export default Channel