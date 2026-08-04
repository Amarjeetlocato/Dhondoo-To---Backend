# 🎯 Flutter Frontend Integration Guide

## 📱 Complete Flutter Implementation Guide for User Registry

---

## 📋 Prerequisites & Setup

### Required Dependencies

Add to `pubspec.yaml`:

```yaml
dependencies:
  flutter:
    sdk: flutter
  http: ^1.1.0
  flutter_secure_storage: ^9.0.0
  shared_preferences: ^2.2.0
  provider: ^6.0.0
  intl: ^0.19.0

dev_dependencies:
  flutter_test:
    sdk: flutter
```

Install dependencies:
```bash
flutter pub get
```

### Network Configuration

**Android** - `android/app/src/main/AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**iOS** - `ios/Runner/Info.plist`:
```xml
<key>NSLocalNetworkUsageDescription</key>
<string>This app connects to local development server</string>
<key>NSBonjourServiceTypes</key>
<array>
  <string>_http._tcp</string>
</array>
```

---

## 🔗 API Configuration

### Constants File

Create `lib/constants/api_constants.dart`:

```dart
class ApiConstants {
  // ⚠️ CRITICAL: Fixed ports - DO NOT CHANGE
  static const String BASE_URL = 'http://localhost:8080';
  static const String API_PREFIX = '/api/auth';
  
  // Complete endpoints
  static const String REGISTER = '$BASE_URL$API_PREFIX/register';
  static const String LOGIN = '$BASE_URL$API_PREFIX/login';
  static const String VERIFY_OTP = '$BASE_URL$API_PREFIX/verify-otp';
  static const String HOME = '$BASE_URL$API_PREFIX/home';
  
  // Token storage keys
  static const String TOKEN_KEY = 'jwtToken';
  static const String USERNAME_KEY = 'username';
  static const String EMAIL_KEY = 'userEmail';
  
  // Token validity
  static const int TOKEN_EXPIRY_MS = 18000000; // 5 hours in milliseconds
}
```

---

## 🔐 Secure Storage Service

Create `lib/services/storage_service.dart`:

```dart
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class StorageService {
  static const _storage = FlutterSecureStorage();

  // ============== JWT Token ==============
  static Future<void> saveToken(String token) async {
    await _storage.write(
      key: 'jwtToken',
      value: token,
    );
  }

  static Future<String?> getToken() async {
    return await _storage.read(key: 'jwtToken');
  }

  static Future<void> deleteToken() async {
    await _storage.delete(key: 'jwtToken');
  }

  // ============== User Info ==============
  static Future<void> saveUserInfo(String username, String email) async {
    await _storage.write(key: 'username', value: username);
    await _storage.write(key: 'userEmail', value: email);
  }

  static Future<String?> getUsername() async {
    return await _storage.read(key: 'username');
  }

  static Future<String?> getEmail() async {
    return await _storage.read(key: 'userEmail');
  }

  static Future<void> deleteUserInfo() async {
    await _storage.delete(key: 'username');
    await _storage.delete(key: 'userEmail');
  }

  // ============== Clear All ==============
  static Future<void> clearAll() async {
    await _storage.deleteAll();
  }

  // ============== Auth Status ==============
  static Future<bool> isAuthenticated() async {
    final token = await getToken();
    return token != null && token.isNotEmpty;
  }
}
```

---

## 📡 API Service

Create `lib/services/api_service.dart`:

```dart
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../constants/api_constants.dart';
import 'storage_service.dart';

class ApiService {
  
  // ============== HELPER METHODS ==============
  
  /// Add headers for protected endpoints
  static Future<Map<String, String>> _getProtectedHeaders() async {
    final token = await StorageService.getToken();
    
    if (token == null) {
      throw Exception('No authentication token found');
    }

    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    };
  }

  /// Add headers for public endpoints (NO TOKEN)
  static Map<String, String> _getPublicHeaders() {
    return {
      'Content-Type': 'application/json',
    };
  }

  /// Handle API response
  static Map<String, dynamic> _handleResponse(http.Response response) {
    try {
      final data = jsonDecode(response.body);
      
      return {
        'statusCode': response.statusCode,
        'data': data,
        'success': data['success'] ?? false,
        'message': data['message'] ?? 'Unknown error',
      };
    } catch (e) {
      return {
        'statusCode': response.statusCode,
        'data': null,
        'success': false,
        'message': 'Failed to parse response: $e',
      };
    }
  }

  // ============== REGISTER ENDPOINT ==============
  
  static Future<Map<String, dynamic>> register({
    required String fullName,
    required String email,
    required String password,
    required String phone,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(ApiConstants.REGISTER),
        headers: _getPublicHeaders(),  // NO TOKEN
        body: jsonEncode({
          'fullName': fullName,
          'email': email,
          'password': password,
          'phone': phone,
        }),
      );

      return _handleResponse(response);
    } catch (e) {
      return {
        'statusCode': 0,
        'data': null,
        'success': false,
        'message': 'Network error: $e',
      };
    }
  }

  // ============== VERIFY OTP ENDPOINT ==============
  
  static Future<Map<String, dynamic>> verifyOtp({
    required String email,
    required String otp,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(ApiConstants.VERIFY_OTP),
        headers: _getPublicHeaders(),  // NO TOKEN
        body: jsonEncode({
          'email': email,
          'otp': otp,
        }),
      );

      return _handleResponse(response);
    } catch (e) {
      return {
        'statusCode': 0,
        'data': null,
        'success': false,
        'message': 'Network error: $e',
      };
    }
  }

  // ============== LOGIN ENDPOINT ==============
  
  static Future<Map<String, dynamic>> login({
    required String email,
    required String password,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(ApiConstants.LOGIN),
        headers: _getPublicHeaders(),  // NO TOKEN
        body: jsonEncode({
          'email': email,
          'password': password,
        }),
      );

      final result = _handleResponse(response);

      // If login successful, save token
      if (result['success'] && response.statusCode == 200) {
        final responseData = result['data']['data'];
        final token = responseData['jwtToken'];
        final username = responseData['username'];

        await StorageService.saveToken(token);
        await StorageService.saveUserInfo(username, email);
      }

      return result;
    } catch (e) {
      return {
        'statusCode': 0,
        'data': null,
        'success': false,
        'message': 'Network error: $e',
      };
    }
  }

  // ============== PROTECTED ENDPOINT: HOME ==============
  
  static Future<Map<String, dynamic>> getHome() async {
    try {
      final headers = await _getProtectedHeaders();  // WITH TOKEN
      
      final response = await http.get(
        Uri.parse(ApiConstants.HOME),
        headers: headers,
      );

      final result = _handleResponse(response);

      // If token expired (401), clear storage
      if (response.statusCode == 401) {
        await StorageService.clearAll();
      }

      return result;
    } catch (e) {
      return {
        'statusCode': 0,
        'data': null,
        'success': false,
        'message': 'Network error: $e',
      };
    }
  }

  // ============== LOGOUT ==============
  
  static Future<void> logout() async {
    await StorageService.clearAll();
  }
}
```

---

## 🎨 UI Components

### Register Screen

Create `lib/screens/auth/register_screen.dart`:

```dart
import 'package:flutter/material.dart';
import '../../services/api_service.dart';
import '../../constants/api_constants.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({Key? key}) : super(key: key);

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _fullNameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _phoneController = TextEditingController();

  bool _isLoading = false;
  Map<String, String> _fieldErrors = {};

  @override
  void dispose() {
    _fullNameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _phoneController.dispose();
    super.dispose();
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red,
      ),
    );
  }

  void _showSuccess(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.green,
      ),
    );
  }

  Future<void> _handleRegister() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);
    _fieldErrors.clear();

    final result = await ApiService.register(
      fullName: _fullNameController.text,
      email: _emailController.text,
      password: _passwordController.text,
      phone: _phoneController.text,
    );

    if (!mounted) return;

    setState(() => _isLoading = false);

    if (result['success']) {
      _showSuccess('Registration successful! Please verify OTP.');
      
      // Navigate to OTP verification screen
      Navigator.pushReplacementNamed(
        context,
        '/verify-otp',
        arguments: _emailController.text,
      );
    } else {
      // Handle validation errors
      if (result['data'] is Map && result['data'].containsKey('data')) {
        final errors = result['data']['data'];
        if (errors is Map) {
          setState(() {
            _fieldErrors = Map<String, String>.from(errors);
          });
        }
      }
      
      _showError(result['message']);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Register')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              // Full Name
              TextFormField(
                controller: _fullNameController,
                decoration: InputDecoration(
                  labelText: 'Full Name',
                  errorText: _fieldErrors['fullName'],
                  border: OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Full name is required';
                  }
                  if (value.length < 3 || value.length > 50) {
                    return 'Name must be 3-50 characters';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Email
              TextFormField(
                controller: _emailController,
                decoration: InputDecoration(
                  labelText: 'Email',
                  errorText: _fieldErrors['email'],
                  border: OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Email is required';
                  }
                  if (!RegExp(r'^[^@]+@[^@]+\.[^@]+').hasMatch(value)) {
                    return 'Invalid email format';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Password
              TextFormField(
                controller: _passwordController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: 'Password',
                  errorText: _fieldErrors['password'],
                  border: OutlineInputBorder(),
                  hintText: 'Min 8 characters',
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Password is required';
                  }
                  if (value.length < 8) {
                    return 'Password must be at least 8 characters';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Phone
              TextFormField(
                controller: _phoneController,
                decoration: InputDecoration(
                  labelText: 'Phone (10 digits)',
                  errorText: _fieldErrors['phone'],
                  border: OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Phone is required';
                  }
                  if (!RegExp(r'^\d{10}$').hasMatch(value)) {
                    return 'Phone must be exactly 10 digits';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 24),

              // Register Button
              ElevatedButton(
                onPressed: _isLoading ? null : _handleRegister,
                child: _isLoading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(),
                      )
                    : const Text('Register'),
              ),

              const SizedBox(height: 16),
              TextButton(
                onPressed: () => Navigator.pushNamed(context, '/login'),
                child: const Text('Already have an account? Login'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

### OTP Verification Screen

Create `lib/screens/auth/verify_otp_screen.dart`:

```dart
import 'package:flutter/material.dart';
import '../../services/api_service.dart';

class VerifyOtpScreen extends StatefulWidget {
  final String email;

  const VerifyOtpScreen({Key? key, required this.email}) : super(key: key);

  @override
  State<VerifyOtpScreen> createState() => _VerifyOtpScreenState();
}

class _VerifyOtpScreenState extends State<VerifyOtpScreen> {
  final _otpController = TextEditingController();
  bool _isLoading = false;
  int _timeLeft = 300; // 5 minutes

  @override
  void initState() {
    super.initState();
    _startTimer();
  }

  void _startTimer() {
    Future.delayed(const Duration(seconds: 1), () {
      if (_timeLeft > 0 && mounted) {
        setState(() => _timeLeft--);
        _startTimer();
      }
    });
  }

  String _formatTime(int seconds) {
    int minutes = seconds ~/ 60;
    int secs = seconds % 60;
    return '$minutes:${secs.toString().padLeft(2, '0')}';
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  Future<void> _handleVerifyOtp() async {
    if (_otpController.text.isEmpty) {
      _showError('Please enter OTP');
      return;
    }

    if (_otpController.text.length != 6) {
      _showError('OTP must be 6 digits');
      return;
    }

    setState(() => _isLoading = true);

    final result = await ApiService.verifyOtp(
      email: widget.email,
      otp: _otpController.text,
    );

    if (!mounted) return;
    setState(() => _isLoading = false);

    if (result['success']) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('OTP verified! Please login.'),
          backgroundColor: Colors.green,
        ),
      );

      Navigator.pushReplacementNamed(context, '/login');
    } else {
      _showError(result['message']);
    }
  }

  @override
  void dispose() {
    _otpController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Verify OTP')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Enter OTP sent to\n${widget.email}',
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 32),

            // OTP Input
            TextField(
              controller: _otpController,
              maxLength: 6,
              keyboardType: TextInputType.number,
              decoration: InputDecoration(
                labelText: '6-digit OTP',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),

            // Timer
            Text(
              'Time left: ${_formatTime(_timeLeft)}',
              style: TextStyle(
                fontSize: 16,
                color: _timeLeft < 60 ? Colors.red : Colors.black,
              ),
            ),
            const SizedBox(height: 24),

            // Verify Button
            ElevatedButton(
              onPressed:
                  _isLoading || _timeLeft == 0 ? null : _handleVerifyOtp,
              child: _isLoading
                  ? const SizedBox(
                      height: 20,
                      width: 20,
                      child: CircularProgressIndicator(),
                    )
                  : const Text('Verify OTP'),
            ),

            if (_timeLeft == 0)
              Padding(
                padding: const EdgeInsets.only(top: 16),
                child: Column(
                  children: [
                    const Text('OTP expired', style: TextStyle(color: Colors.red)),
                    TextButton(
                      onPressed: () => Navigator.pushReplacementNamed(
                        context,
                        '/register',
                      ),
                      child: const Text('Register again'),
                    ),
                  ],
                ),
              ),
          ],
        ),
      ),
    );
  }
}
```

### Login Screen

Create `lib/screens/auth/login_screen.dart`:

```dart
import 'package:flutter/material.dart';
import '../../services/api_service.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({Key? key}) : super(key: key);

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();

  bool _isLoading = false;
  Map<String, String> _fieldErrors = {};

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  Future<void> _handleLogin() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);
    _fieldErrors.clear();

    final result = await ApiService.login(
      email: _emailController.text,
      password: _passwordController.text,
    );

    if (!mounted) return;
    setState(() => _isLoading = false);

    if (result['success']) {
      Navigator.pushReplacementNamed(context, '/home');
    } else {
      if (result['data'] is Map && result['data'].containsKey('data')) {
        final errors = result['data']['data'];
        if (errors is Map) {
          setState(() {
            _fieldErrors = Map<String, String>.from(errors);
          });
        }
      }

      _showError(result['message']);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Login')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: _emailController,
                decoration: InputDecoration(
                  labelText: 'Email',
                  errorText: _fieldErrors['email'],
                  border: OutlineInputBorder(),
                ),
                validator: (value) => value?.isEmpty ?? true
                    ? 'Email is required'
                    : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _passwordController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: 'Password',
                  errorText: _fieldErrors['password'],
                  border: OutlineInputBorder(),
                ),
                validator: (value) => value?.isEmpty ?? true
                    ? 'Password is required'
                    : null,
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _isLoading ? null : _handleLogin,
                child: _isLoading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(),
                      )
                    : const Text('Login'),
              ),
              const SizedBox(height: 16),
              TextButton(
                onPressed: () => Navigator.pushNamed(context, '/register'),
                child: const Text('Don\'t have an account? Register'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

### Home Screen

Create `lib/screens/home_screen.dart`:

```dart
import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../services/storage_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  late Future<Map<String, dynamic>> _homeDataFuture;

  @override
  void initState() {
    super.initState();
    _homeDataFuture = _loadHomeData();
  }

  Future<Map<String, dynamic>> _loadHomeData() async {
    final result = await ApiService.getHome();
    
    if (result['statusCode'] == 401) {
      // Token expired
      if (mounted) {
        Navigator.pushReplacementNamed(context, '/login');
      }
    }
    
    return result;
  }

  Future<void> _handleLogout() async {
    await ApiService.logout();
    if (mounted) {
      Navigator.pushReplacementNamed(context, '/login');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Home'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _handleLogout,
          ),
        ],
      ),
      body: FutureBuilder<Map<String, dynamic>>(
        future: _homeDataFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (!snapshot.hasData || !snapshot.data!['success']) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(snapshot.data?['message'] ?? 'Error loading data'),
                  ElevatedButton(
                    onPressed: () {
                      setState(() {
                        _homeDataFuture = _loadHomeData();
                      });
                    },
                    child: const Text('Retry'),
                  ),
                ],
              ),
            );
          }

          final data = snapshot.data!['data']['data'];

          return Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                Text(
                  'Welcome, ${data['email']}',
                  style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                Text(data['message']),
                const SizedBox(height: 32),
                const Text(
                  'Features:',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                ...List.from(data['features']).map((feature) {
                  return Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(feature),
                    ),
                  );
                }),
              ],
            ),
          );
        },
      ),
    );
  }
}
```

---

## 🧭 Navigation Setup

Create `lib/main.dart`:

```dart
import 'package:flutter/material.dart';
import 'screens/auth/register_screen.dart';
import 'screens/auth/login_screen.dart';
import 'screens/auth/verify_otp_screen.dart';
import 'screens/home_screen.dart';
import 'services/storage_service.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'User Registry',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const SplashScreen(),
      routes: {
        '/register': (context) => const RegisterScreen(),
        '/login': (context) => const LoginScreen(),
        '/verify-otp': (context) {
          final email = ModalRoute.of(context)!.settings.arguments as String;
          return VerifyOtpScreen(email: email);
        },
        '/home': (context) => const HomeScreen(),
      },
    );
  }
}

class SplashScreen extends StatefulWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    _checkAuth();
  }

  Future<void> _checkAuth() async {
    await Future.delayed(const Duration(seconds: 2));

    final isAuthenticated = await StorageService.isAuthenticated();

    if (mounted) {
      Navigator.pushReplacementNamed(
        context,
        isAuthenticated ? '/home' : '/login',
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const [
            Icon(Icons.app_registration, size: 64),
            SizedBox(height: 16),
            Text('User Registry'),
            SizedBox(height: 32),
            CircularProgressIndicator(),
          ],
        ),
      ),
    );
  }
}
```

---

## ✅ Flutter Implementation Checklist

- [ ] All dependencies added to pubspec.yaml
- [ ] API constants defined correctly (port 8080)
- [ ] StorageService implemented with FlutterSecureStorage
- [ ] ApiService implemented with all endpoints
- [ ] Register screen with validation
- [ ] OTP verification screen with timer
- [ ] Login screen
- [ ] Home screen (protected)
- [ ] Navigation configured
- [ ] Token stored after login
- [ ] Token included in protected requests
- [ ] Token cleared on logout
- [ ] 401 errors handled (logout)
- [ ] Field validation errors displayed
- [ ] Loading states implemented
- [ ] Error messages shown to user

---

## 🚀 Running Flutter App

**Start the app on port 56789**:
```bash
flutter run -d chrome --web-port=56789
```

**Ensure backend is running**:
```bash
# In another terminal
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

---

Generated: May 25, 2026
Version: 1.0
For: Flutter Frontend Developers
