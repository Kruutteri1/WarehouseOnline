import React, {useState} from 'react';
import Home from "./Home";
import Registration from "./components/Register/Registration";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from "./components/navbar/Navbar";
import Authenticate from "./components/Register/Authenticate";
import Warehouse from "./components/Warehouse/Warehouse";
import Orders from "./components/Orders/Orders";

function App() {
    return (
        <Router>
            <Navbar />
            <Routes>
                <Route path='/' element={<Home />} />
                <Route path='/registration' element={<Registration />} />
                <Route path='/authenticate' element={<Authenticate />} />
                <Route path="/Orders" element={<Orders />} />
                <Route path='/Warehouse' element={<Warehouse />} />

            </Routes>
        </Router>
    );
}

export default App;
