import React,{createContext, useContext, useState, useEffect} from 'react';
import api from '../api/axios';
import {Navigate} from 'react-router-dom';

const AuthContext = createContext(null);

export const AuthProvider = ({children}) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [isRegistered, setIsRegistered] = useState(false);
    const [token, setToken] = useState(null);
    const [user, setUser] = useState(null);
    const [email, setEmail] = useState(null);

    const [cart, setCart] = useState([]);

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        const storedEmail = localStorage.getItem('email');
        const verifyToken = async () => {
            try {
                setIsLoading(true);
                if(storedToken) {
                    api.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
                    const response = await api.get('/api/auth/user/role');
                    setUser(response.data);
                    setToken(storedToken);
                    setIsAuthenticated(true);
                    setEmail(storedEmail || response.data.email);
                    console.log(response.data);
                    const cartResponse = await api.get('/api/cart');
                    setCart(cartResponse.data.items || []);
                } else {
                    logout();
                }
            } catch (error) {
                console.error('Token verification failed', error);
                logout();
            } finally {
                setIsLoading(false);
            }
        };
        verifyToken();
    }, [token]);


    const login = async (loginData) => {
        try {
            const response = await api.post('/api/auth/login', loginData);
            const token = response.data;
            const userEmail = loginData.email;
            localStorage.setItem('token', token);
            localStorage.setItem('email', userEmail);
            api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

            setToken(token);
            setIsAuthenticated(true);

            // Fetch and set cart on login
            const cartResponse = await api.get('/api/cart');
            setCart(cartResponse.data.items || []); 

            return {success: true};
        } catch (error) {
            console.error('Login failed', error);
            return {
                success: false,
                message: error.response?.data?.message || 'Login failed'
            };
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('email');
        delete api.defaults.headers.common['Authorization'];
        setIsAuthenticated(false);
        setToken(null);
        setUser(null);
        setEmail(null);
        setCart([]); // Clear cart on logout
    }

    const addToCart = async (productId, quantity) => {
        try {
            const response = await api.post('/api/cart/add', null, {
                params: {productId, quantity}
            });
            setCart(response.data.items); // Update cart state with the new items
        } catch (error) {
            console.error('Error adding to cart:', error);
        }
    };

    const removeFromCart = async (productId) => {
        try {
            await api.delete(`/api/cart/${productId}`);
            setCart(cart.filter(item => item.productId != productId));
        } catch(error) {
            console.error('Error removing from cart:', error);
        }
    };

    const clearCart = async () => {
        try {
            await api.delete('/api/cart');
            setCart([]);
        } catch(error) {
            console.error('Error clearing cart:', error);
        }
    };

    const PrivateRoute = ({children}) => {
        if (isLoading) {
            return <div>Loding...</div>;
        }
        return isAuthenticated ? children : <Navigate to="/login" />;
    };

    const createOrder = async (address, phoneNumber) => {
        try {
            const response = await api.post('/api/orders', null, {params: {address, phoneNumber} });
            return response.data;
        } catch (error) {
            console.error('Error creating order:', error);
            throw error;
        }
    };

    const getAllOrders = async () => {
        try {
            const response = await api.get('/api/orders');
            return response.data;
        } catch (error) {
            console.error('Error fetching all orders:', error);
            throw error;
        }
    };

    const getUserOrders = async () => {
        try {
            const response = await api.get('/api/orders/user');
            return response.data;
        } catch (error) {
            console.error('Error fetching user orders:', error);
            throw error;
        }
    };

    const updateOrderStatus = async (orderId, status) => {
        try {
            const response = await api.put(`/api/orders/${orderId}/status`, null, {params: {status} });
            return response.data;
        } catch (error) {
            console,error('Error updating order status:', error);
            throw error;
        }
    };

    return (
        <AuthContext.Provider value={{
            isAuthenticated,
            setIsAuthenticated,
            isRegistered,
            setIsRegistered,
            isLoading,
            token,
            setToken,
            user,
            setUser,
            cart,
            addToCart,
            removeFromCart,
            clearCart,
            logout,
            login,
            PrivateRoute,
            createOrder,
            getAllOrders,
            getUserOrders,
            updateOrderStatus
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);