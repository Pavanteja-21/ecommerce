import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios'; // Assuming you have an API service setup

export const Navbar = () => {
    const { isAuthenticated, user, logout} = useAuth();
    const navigate = useNavigate();
    const [isEmailConfirmed, setIsEmailConfirmed] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    const checkEmailConfirmation = async () => {
        try {
            setIsLoading(true);
            const response = await api.get('/api/auth/user/confirmation');
            console.log('Confirmation Response:', response.data);
            if (response.data) {
                setIsEmailConfirmed(true);
                console.log('Email confirmed, navigating to /');
                navigate('/', {replace: true});
            } else {
                setIsEmailConfirmed(false)
            }
        } catch (err) {
            console.error('Error checking email confirmation:', err.response?.status, err.response?.data);
            if (err.response && (err.response.status === 403 || err.response.status === 404)) {
                console.log('Redirecting to /login due to 403/404');
                navigate('/login', { replace: true });
            } else {
                console.error('Other error:', err);
                setIsEmailConfirmed(false); 
            }
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (isAuthenticated) {
            checkEmailConfirmation();
        }
    }, [isAuthenticated]);

    const handleLoggout = () => {
        logout();
        navigate('/login');
    };

    const toggleMenu = () => {
       setIsMenuOpen(!isMenuOpen);
    }

    return (
        <nav className="navbar navbar-expand-lg navbar-light bg-light">
            <div className="container-fluid">
                <Link className="navbar-brand" to="/">My App</Link>
                <button
                    className="navbar-toggler"
                    type="button"
                    onClick={toggleMenu}
                    aria-controls="navbarNav"
                    aria-expanded={isMenuOpen}
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className={`collapse navbar-collapse ${isMenuOpen ? 'show' : ''}`} id="navbarNav">
                    <div className="navbar-nav ms-auto">
                        {isAuthenticated ? (
                            <>
                                {user?.includes('ADMIN') && (
                                    <Link className="btn btn-success me-2" to="/admin">
                                        Admin Panel
                                    </Link>
                                )}
                                {!isEmailConfirmed && (
                                    <Link className="btn btn-secondary me-2" to="/confirm-email">
                                        Confirm Email
                                    </Link>
                                )}
                                <Link className="btn btn-secondary me-2" to="/orders">
                                    Orders
                                </Link>
                                <Link className="btn btn-secondary me-2" to="/change-password">
                                    Change Password
                                </Link>
                                <Link className="btn btn-secondary me-2" to="/cart">
                                    Cart
                                </Link>
                                <button
                                    className="btn btn-outline-danger"
                                    onClick={handleLogout}
                                >
                                    Logout
                                </button>
                            </>
                        ) : (
                            <>
                                <Link className="nav-item nav-link" to="/login">Login</Link>
                                <Link className="nav-item nav-link" to="/register">Register</Link>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
 
}


