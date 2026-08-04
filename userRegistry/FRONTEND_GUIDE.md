# 🚀 Frontend Integration - Quick Start Guide

## 📚 Quick Reference

### Base Configuration for Frontend
```javascript
const API_CONFIG = {
  BASE_URL: 'http://localhost:8080',
  API_PREFIX: '/api/auth',
  TOKEN_KEY: 'jwtToken',
  USERNAME_KEY: 'username',
  TOKEN_EXPIRY: 18000000 // 5 hours in ms
};

const API_ENDPOINTS = {
  REGISTER: '/register',
  LOGIN: '/login',
  VERIFY_OTP: '/verify-otp',
  HOME: '/home'
};
```

---

## 📋 Endpoint Quick Reference

| Endpoint | Method | Auth | Purpose |
|----------|--------|------|---------|
| `/api/auth/register` | POST | ❌ | Create new account |
| `/api/auth/login` | POST | ❌ | Get JWT token |
| `/api/auth/verify-otp` | POST | ❌ | Activate account |
| `/api/auth/home` | GET | ✅ | Get user info |

---

## 🎯 Request/Response Examples

### 1. REGISTER
```
Request:
POST /api/auth/register
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "phone": "9876543210"
}

Response (201):
{
  "success": true,
  "message": "OTP sent to phone/email",
  "data": null
}
```

### 2. VERIFY OTP
```
Request:
POST /api/auth/verify-otp
{
  "email": "john@example.com",
  "otp": "123456"
}

Response (200):
{
  "success": true,
  "message": "Verified successfully",
  "data": null
}
```

### 3. LOGIN
```
Request:
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "SecurePass123"
}

Response (200):
{
  "success": true,
  "message": "Login successful",
  "data": {
    "jwtToken": "eyJhbGc...",
    "username": "john@example.com"
  }
}
```

### 4. HOME (Protected)
```
Request:
GET /api/auth/home
Headers:
  Authorization: Bearer eyJhbGc...

Response (200):
{
  "success": true,
  "message": "Welcome",
  "data": {
    "email": "john@example.com",
    "message": "Welcome to Home",
    "features": ["Reels", "Shop", "Chat"]
  }
}
```

---

## 🔑 Input Validation Rules

### Registration Fields
```
fullName: 
  - Required
  - Min: 3 chars, Max: 50 chars
  - Letters and spaces only

email: 
  - Required
  - Valid email format (xxx@xxx.xxx)
  - Must be unique

password: 
  - Required
  - Min: 8 chars, Max: 100 chars
  - No validation for complexity (backend allows any 8+ char)

phone: 
  - Required
  - Exactly 10 digits
  - Numbers only (e.g., 9876543210)
```

### Login Fields
```
email: 
  - Required
  - Valid email format

password: 
  - Required
  - Min: 8 chars, Max: 100 chars
```

### OTP Verification Fields
```
email: 
  - Required
  - Valid email format

otp: 
  - Required
  - Exactly 6 digits
```

---

## 💻 React Component Examples

### Register Component
```jsx
import React, { useState } from 'react';

const Register = ({ onRegisterSuccess }) => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    phone: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrors({});

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      const data = await response.json();

      if (data.success) {
        // Call parent callback with email for OTP verification
        onRegisterSuccess(formData.email);
      } else {
        // Handle validation errors
        if (data.data && typeof data.data === 'object') {
          setErrors(data.data);
        } else {
          setErrors({ general: data.message });
        }
      }
    } catch (error) {
      setErrors({ general: 'Network error: ' + error.message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        name="fullName"
        placeholder="Full Name"
        value={formData.fullName}
        onChange={handleChange}
      />
      {errors.fullName && <span className="error">{errors.fullName}</span>}

      <input
        type="email"
        name="email"
        placeholder="Email"
        value={formData.email}
        onChange={handleChange}
      />
      {errors.email && <span className="error">{errors.email}</span>}

      <input
        type="password"
        name="password"
        placeholder="Password (min 8 chars)"
        value={formData.password}
        onChange={handleChange}
      />
      {errors.password && <span className="error">{errors.password}</span>}

      <input
        type="tel"
        name="phone"
        placeholder="Phone (10 digits)"
        value={formData.phone}
        onChange={handleChange}
      />
      {errors.phone && <span className="error">{errors.phone}</span>}

      {errors.general && <div className="error">{errors.general}</div>}

      <button type="submit" disabled={loading}>
        {loading ? 'Registering...' : 'Register'}
      </button>
    </form>
  );
};

export default Register;
```

### OTP Verification Component
```jsx
import React, { useState, useEffect } from 'react';

const VerifyOtp = ({ email, onVerifySuccess }) => {
  const [otp, setOtp] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [timeLeft, setTimeLeft] = useState(300); // 5 minutes

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await fetch('http://localhost:8080/api/auth/verify-otp', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: email,
          otp: otp
        })
      });

      const data = await response.json();

      if (data.success) {
        onVerifySuccess();
      } else {
        setError(data.message || 'OTP verification failed');
      }
    } catch (error) {
      setError('Network error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const minutes = Math.floor(timeLeft / 60);
  const seconds = timeLeft % 60;

  return (
    <form onSubmit={handleSubmit}>
      <p>Enter OTP sent to {email}</p>
      <p className="timer">Time left: {minutes}:{seconds < 10 ? '0' : ''}{seconds}</p>

      <input
        type="text"
        placeholder="Enter 6-digit OTP"
        value={otp}
        onChange={(e) => setOtp(e.target.value)}
        maxLength="6"
      />
      {error && <div className="error">{error}</div>}

      <button type="submit" disabled={loading || timeLeft === 0}>
        {loading ? 'Verifying...' : 'Verify OTP'}
      </button>

      {timeLeft === 0 && (
        <p className="error">OTP expired. Please register again.</p>
      )}
    </form>
  );
};

export default VerifyOtp;
```

### Login Component
```jsx
import React, { useState } from 'react';

const Login = ({ onLoginSuccess }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrors({});

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      const data = await response.json();

      if (data.success) {
        // Store token
        localStorage.setItem('jwtToken', data.data.jwtToken);
        localStorage.setItem('username', data.data.username);
        onLoginSuccess();
      } else {
        if (data.data && typeof data.data === 'object') {
          setErrors(data.data);
        } else {
          setErrors({ general: data.message });
        }
      }
    } catch (error) {
      setErrors({ general: 'Network error: ' + error.message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        name="email"
        placeholder="Email"
        value={formData.email}
        onChange={handleChange}
      />
      {errors.email && <span className="error">{errors.email}</span>}

      <input
        type="password"
        name="password"
        placeholder="Password"
        value={formData.password}
        onChange={handleChange}
      />
      {errors.password && <span className="error">{errors.password}</span>}

      {errors.general && <div className="error">{errors.general}</div>}

      <button type="submit" disabled={loading}>
        {loading ? 'Logging in...' : 'Login'}
      </button>
    </form>
  );
};

export default Login;
```

### Protected Component (Home)
```jsx
import React, { useState, useEffect } from 'react';

const Home = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchUserInfo = async () => {
      const token = localStorage.getItem('jwtToken');

      if (!token) {
        setError('Not authenticated');
        setLoading(false);
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/api/auth/home', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        const data = await response.json();

        if (data.success) {
          setUserInfo(data.data);
        } else {
          if (response.status === 401) {
            // Token expired
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('username');
          }
          setError(data.message);
        }
      } catch (err) {
        setError('Network error: ' + err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('username');
    window.location.href = '/login';
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      <h1>Welcome, {userInfo?.email}</h1>
      <p>{userInfo?.message}</p>
      <div className="features">
        <h3>Features:</h3>
        <ul>
          {userInfo?.features?.map((feature, index) => (
            <li key={index}>{feature}</li>
          ))}
        </ul>
      </div>
      <button onClick={handleLogout}>Logout</button>
    </div>
  );
};

export default Home;
```

### Auth Service (Reusable)
```jsx
// authService.js
export const authService = {
  async register(userData) {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    });
    return response.json();
  },

  async verifyOtp(email, otp) {
    const response = await fetch('http://localhost:8080/api/auth/verify-otp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, otp })
    });
    return response.json();
  },

  async login(email, password) {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    return response.json();
  },

  async getHome() {
    const token = localStorage.getItem('jwtToken');
    if (!token) throw new Error('No token found');

    const response = await fetch('http://localhost:8080/api/auth/home', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    return response.json();
  },

  logout() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('username');
  },

  isAuthenticated() {
    return !!localStorage.getItem('jwtToken');
  },

  getToken() {
    return localStorage.getItem('jwtToken');
  }
};
```

---

## 🌐 Vue.js Examples

### Register Component (Vue 3)
```vue
<template>
  <form @submit.prevent="handleSubmit">
    <input v-model="form.fullName" placeholder="Full Name" />
    <span v-if="errors.fullName" class="error">{{ errors.fullName }}</span>

    <input v-model="form.email" type="email" placeholder="Email" />
    <span v-if="errors.email" class="error">{{ errors.email }}</span>

    <input v-model="form.password" type="password" placeholder="Password" />
    <span v-if="errors.password" class="error">{{ errors.password }}</span>

    <input v-model="form.phone" placeholder="Phone (10 digits)" />
    <span v-if="errors.phone" class="error">{{ errors.phone }}</span>

    <button type="submit" :disabled="loading">
      {{ loading ? 'Registering...' : 'Register' }}
    </button>
  </form>
</template>

<script>
import { ref } from 'vue';

export default {
  emits: ['registerSuccess'],
  setup(props, { emit }) {
    const form = ref({
      fullName: '',
      email: '',
      password: '',
      phone: ''
    });
    const errors = ref({});
    const loading = ref(false);

    const handleSubmit = async () => {
      loading.value = true;
      errors.value = {};

      try {
        const response = await fetch('http://localhost:8080/api/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(form.value)
        });

        const data = await response.json();

        if (data.success) {
          emit('registerSuccess', form.value.email);
        } else {
          errors.value = data.data || { general: data.message };
        }
      } catch (error) {
        errors.value.general = 'Network error: ' + error.message;
      } finally {
        loading.value = false;
      }
    };

    return { form, errors, loading, handleSubmit };
  }
};
</script>
```

---

## 🔄 Error Handling Flowchart

```
API Call
  ↓
Network OK? → No → Show Network Error
  ↓ Yes
Response OK? → No → Show HTTP Error
  ↓ Yes
Parse JSON
  ↓
response.success? → No → Check response.data
  ↓ Yes
  ├─ Is Object? → Show Field Errors
  ├─ Is String? → Show General Message
  └─ Is Null? → Show response.message
  ↓
Handle Success
```

---

## 📌 Common Issues & Solutions

### Issue: 401 Unauthorized
**Cause**: Missing or invalid JWT token
**Solution**: 
- Check if token exists in localStorage
- Verify token format (should start with "eyJ")
- Check if token has expired (5 hours)
- Re-login to get new token

### Issue: 400 Bad Request with Validation Errors
**Cause**: Invalid input data
**Solution**:
- Check email format
- Verify password length (min 8)
- Ensure phone is exactly 10 digits
- Verify fullName is 3-50 chars

### Issue: 409 Conflict
**Cause**: User already exists
**Solution**: 
- Use different email
- Login instead if account exists

### Issue: CORS Error
**Cause**: Cross-Origin Request Blocked
**Solution**:
- Backend CORS is enabled for all origins
- Check if backend is running (http://localhost:8080)
- Use correct API URL

### Issue: OTP Not Received
**Cause**: OTP delivery not implemented (only prints to console)
**Solution**:
- Check backend console for OTP
- Implement SMS/Email service integration

---

## 🎯 Before You Start

1. ✅ Backend must be running on http://localhost:8080
2. ✅ MySQL database must be running (localhost:3306)
3. ✅ Database name: `user` (must exist)
4. ✅ User: `root`, Password: (empty)
5. ✅ Frontend CORS enabled (set in SecurityConfig)

---

## 📝 Checklist for Frontend Implementation

- [ ] Setup React/Vue project
- [ ] Create Register component with validation
- [ ] Create OTP verification component
- [ ] Create Login component
- [ ] Create protected Home component
- [ ] Setup token storage in localStorage
- [ ] Implement auto-logout on token expiry
- [ ] Add error handling for all API calls
- [ ] Test all endpoints with backend
- [ ] Implement password validation UI
- [ ] Add loading states to buttons
- [ ] Style components as per design
- [ ] Test on different browsers
- [ ] Test on mobile devices

---

Generated for Frontend Developers
Backend Version: 1.0
Last Updated: May 25, 2026
