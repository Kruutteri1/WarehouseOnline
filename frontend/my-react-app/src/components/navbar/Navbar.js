import React, { useState, useEffect } from "react";
import {Link, useNavigate} from "react-router-dom";
import {
    BsPersonCircle,
    BsBoxArrowRight
} from "react-icons/bs";
import "./Navbar.css";
import "./profile.css";

const Navbar = () => {
    const [mobile, setMobile] = useState(false);
    const [isOpen, setOpen] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        // Проверка наличия токена в куках при загрузке компонента
        const token = document.cookie
            .split(";")
            .find((cookie) => cookie.trim().startsWith("jwtToken"));

        if (token) {
            setIsAuthenticated(true);
        }
    }, [isAuthenticated, setIsAuthenticated]);

    const deleteCookie = (name) => {
        document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;`;
    };

    const handleLogout = () => {
        deleteCookie("jwtToken"); // Удаление токена из куки
        setIsAuthenticated(false); // Установка состояния авторизации в false
        navigate("/")
    };

    return (
        <>
            <nav className="navbar">
                <h3 className="logo">Logo</h3>
                <ul className={mobile ? "nav-links-mobile" : "nav-links"} onClick={() => setMobile(false)}>
                    <Link to="/" className="home">
                        <li>Home</li>
                    </Link>
                    <Link to="/orders" className="orders">
                        <li>Orders</li>
                    </Link>
                    <Link to="/warehouse" className="warehouse">
                        <li>Warehouse</li>
                    </Link>
                </ul>
                {isAuthenticated ? (
                    <button className="menu-button" onClick={() => setOpen(!isOpen)}>
                        <BsPersonCircle />
                    </button>
                ) : (
                    <div>
                        <Link to="/authenticate" className="login">
                            Log In
                        </Link>{" "}
                        |{" "}
                        <Link to="/registration" className="registration">
                            Registration
                        </Link>
                    </div>
                )}
                {isAuthenticated && (
                    <nav className={`menu ${isOpen ? "active" : ""}`}>
                        <ul className="menu_list">
                            <li className="menu_item" onClick={handleLogout}>
                                <BsBoxArrowRight className="icon" />
                                <span>Logout</span>
                            </li>
                        </ul>
                    </nav>
                )}
            </nav>
        </>
    );
};

export default Navbar;
