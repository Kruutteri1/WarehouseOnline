import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./authenticate.css";
import { setToken } from "../../Token/Token";

function App() {
    const initialValues = { email: "", password: "" };
    const [formValues, setFormValues] = useState(initialValues);
    const [formErrors, setFormErrors] = useState({});
    const [isSubmit, setIsSubmit] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormValues({ ...formValues, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmit(true);

        const errors = validate(formValues);
        setFormErrors(errors);

        if (Object.keys(errors).length === 0) {
            try {
                const response = await fetch("/api/auth/authenticate", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(formValues),
                });

                if (response.ok) {
                    console.log("Sign In successful!");
                    const responseData = await response.json();
                    setToken(responseData.token);

                    navigate("/")
                } else {
                    console.error("Sign In failed.");
                }
            } catch (error) {
                console.error("Error during Sign In:", error);
            }
        }
    };

    const validate = (values) => {
        const errors = {};
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i;
        if (!values.email) {
            errors.email = "Email is required!";
        } else if (!regex.test(values.email)) {
            errors.email = "This is not a valid email format!";
        }
        if (!values.password) {
            errors.password = "Password is required";
        } else if (values.password.length > 10) {
            errors.password = "Password cannot exceed more than 10 characters";
        }
        return errors;
    };

    return (
        <div className="container">
            <form onSubmit={handleSubmit}>
                <div className="ui form">
                    <h1 className="centered">WarehouseOnline</h1>
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
                    <button className="fluid ui button gray">Sign In</button>
                    <div className="sign-in-link">
                        <p className="gray">Create a new account? <Link to="/registration">Sign Up</Link></p>
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