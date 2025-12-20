import { BrowserRouter as Router, Route, Routes, Navigate, useNavigate } from 'react-router-dom';
import { Navbar } from './components/Navbar';

export const App = () => {
  return (
      <Router>
         <Navbar />
      </Router>
  )
}

export default App;
