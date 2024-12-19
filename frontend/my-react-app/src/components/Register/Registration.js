import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./registration.css";

function App() {
    const initialValues = { username: "", email: "", password: "" };
    const [formValues, setFormValues] = useState(initialValues);
    const [formErrors, setFormErrors] = useState({});
    const [isSubmit, setIsSubmit] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormValues({ ...formValues, [name]: value });
    };

    const validate = (values) => {
        const errors = {};
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i;
        if (!values.username) {
            errors.username = "Username is required!";
        }
        if (!values.email) {
            errors.email = "Email is required!";
        } else if (!regex.test(values.email)) {
            errors.email = "This is not a valid email format!";
        }
        if (!values.password) {
            errors.password = "Password is required";
        } else if (values.password.length < 4) {
            errors.password = "Password must be more than 4 characters";
        } else if (values.password.length > 10) {
            errors.password = "Password cannot exceed more than 10 characters";
        }
        return errors;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmit(true);

        const errors = validate(formValues);
        setFormErrors(errors);

        if (Object.keys(errors).length === 0) {
            try {
                const response = await fetch("/api/auth/register", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(formValues),
                });

                if (response.ok) {
                    console.log("Registration successful!");
                    navigate("/authenticate");
                } else {
                    const errorData = await response.json();
                    setErrorMessage(errorData.message || 'An error occurred. Please try again.');
                }
            } catch (error) {
                console.error('Error during fetch:', error);
                setErrorMessage('An unexpected error occurred. Please try again later.');
            }
        }
    };

    return (
        <div className="container">
            <form onSubmit={handleSubmit}>
                <div className="ui form">
                    <h1 className="centered">WarehouseOnline</h1>
                    {errorMessage && <div className="error-message">{errorMessage}</div>}
                    <div className="field">
                        <label>Username</label>
                        {isSubmit && <p>{formErrors.username}</p>}
                        <input
                            type="text"
                            name="username"
                            placeholder="Username"
                            value={formValues.username}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="field">
                        <label>Email</label>
                        {isSubmit && <p>{formErrors.email}</p>}
                        <input
                            type="text"
                            name="email"
                            placeholder="Email"
                            value={formValues.email}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="field">
                        <label>Password</label>
                        {isSubmit && <p>{formErrors.password}</p>}
                        <input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={formValues.password}
                            onChange={handleChange}
                        />
                    </div>
                    <button className="fluid ui button gray">Sign Up</button>
                    <div className="sign-in-link">
                        <p className="gray">Have an account? <Link to="/login">Sign In</Link></p>
                    </div>
                </div>
            </form>
            <div className="footer">
                <p>© 2024 WarehouseOnline. All Rights Reserved.</p>
            </div>
        </div>
    );
}

export default App;