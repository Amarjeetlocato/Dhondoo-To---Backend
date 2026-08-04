# Flutter HTTP Client Configuration - Fix Connection Timeout

## Problem
Your Flutter app is timing out at **5 seconds** before the request reaches the Gateway.

```
DioException [connection timeout]: The request connection took longer than 0:00:05.000000
```

## Solution

Update your Flutter app's HTTP client configuration to have a **30-60 second timeout** instead of 5 seconds.

### Option 1: Using Dio (Most Common)

If you're using the `dio` package, update your API service:

```dart
// lib/services/api_client.dart (or wherever you initialize Dio)

import 'package:dio/dio.dart';

final dio = Dio(
  BaseOptions(
    baseUrl: 'http://10.0.2.2:8079',
    connectTimeout: Duration(seconds: 30),  // INCREASE THIS from 5s to 30s
    receiveTimeout: Duration(seconds: 60),  // INCREASE THIS from 5s to 60s
    sendTimeout: Duration(seconds: 30),     // INCREASE THIS if it exists
    responseType: ResponseType.json,
    headers: {
      'Content-Type': 'application/json',
    },
  ),
);

// Use dio for all requests
Future<dynamic> login(String email, String password) async {
  try {
    final response = await dio.post(
      '/api/auth/login',
      data: {
        'usernameOrEmail': email,
        'password': password,
      },
    );
    return response.data;
  } on DioException catch (e) {
    // Handle error
    print('API Error: ${e.message}');
    rethrow;
  }
}
```

### Option 2: Using http Package

If you're using the standard `http` package:

```dart
// lib/services/http_client.dart

import 'package:http/http.dart' as http;

final client = http.Client();

Future<dynamic> login(String email, String password) async {
  try {
    final response = await client.post(
      Uri.parse('http://10.0.2.2:8079/api/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'usernameOrEmail': email,
        'password': password,
      }),
    ).timeout(
      Duration(seconds: 60), // INCREASE timeout to 60 seconds
      onTimeout: () => throw TimeoutException('Request timed out'),
    );
    
    return jsonDecode(response.body);
  } catch (e) {
    print('API Error: $e');
    rethrow;
  }
}
```

### Option 3: Find Your API Service File

Search your Flutter project for:
- `connectTimeout: Duration(seconds: 5)`
- `receiveTimeout: Duration(seconds: 5)`
- `.timeout(Duration(seconds: 5))`

Replace `5` with `30` or `60`.

---

## Files to Update

Common Flutter API service files:
1. `lib/services/api_client.dart`
2. `lib/services/api_service.dart`
3. `lib/services/http_service.dart`
4. `lib/data/services/api_client.dart`
5. `lib/utils/dio_client.dart`
6. Any file with `Dio()` initialization or `http.Client()` setup

---

## Example: Complete Fix for Dio

```dart
import 'package:dio/dio.dart';

class ApiClient {
  static final Dio _dio = Dio(
    BaseOptions(
      baseUrl: 'http://10.0.2.2:8079',
      connectTimeout: Duration(seconds: 30),  // ← CHANGE: was 5s
      receiveTimeout: Duration(seconds: 60),  // ← CHANGE: was 5s
      sendTimeout: Duration(seconds: 30),     // ← CHANGE: was 5s
      responseType: ResponseType.json,
      validateStatus: (status) => status != null,
    ),
  );

  static Dio get instance => _dio;

  static Future<Response> post(String endpoint, Map<String, dynamic> data) async {
    try {
      final response = await _dio.post(endpoint, data: data);
      return response;
    } on DioException catch (e) {
      print('API Error: ${e.message}');
      rethrow;
    }
  }

  static Future<Response> get(String endpoint) async {
    try {
      final response = await _dio.get(endpoint);
      return response;
    } on DioException catch (e) {
      print('API Error: ${e.message}');
      rethrow;
    }
  }
}

// Usage in your login screen:
// ApiClient.post('/api/auth/login', {
//   'usernameOrEmail': email,
//   'password': password,
// });
```

---

## Quick Fix Steps

1. **Find** your API service file (search for `Dio()` or `http.Client()`)
2. **Change** `Duration(seconds: 5)` → `Duration(seconds: 30)`
3. **Change** `Duration(milliseconds: 5000)` → `Duration(milliseconds: 30000)`
4. **Save** the file
5. **Hot reload** or **restart** your Flutter app
6. **Test** login again

---

## Result After Fix

After updating timeouts, you should see:
- ✓ Initial connection delay (Gateway bootstrapping)
- ✓ Request reaches Gateway at 10.0.2.2:8079
- ✓ Gateway routes to microservices
- ✓ Response returns successfully
- ✓ Login completes or shows actual API error (not timeout)

---

## Gateway Verification

Your Gateway is running correctly with proper timeouts:
- ✓ Connect timeout: **10 seconds**
- ✓ Response timeout: **60 seconds**
- ✓ Listening on: **0.0.0.0:8079**
- ✓ CORS enabled for: **10.0.2.2**

The problem was 100% in the Flutter app's HTTP client configuration!
