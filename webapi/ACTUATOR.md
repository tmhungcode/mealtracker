# 🏥 Health Check & Monitoring with Spring Boot Actuator

## ✅ What Was Implemented

### **1. Spring Boot Actuator Added**

- ✅ Added `spring-boot-starter-actuator` dependency
- ✅ Configured public health and info endpoints
- ✅ Secured other actuator endpoints (require authentication)

### **2. Public Endpoints**

| Endpoint           | Method | Authentication | Description               |
|--------------------|--------|----------------|---------------------------|
| `/actuator/health` | GET    | ❌ Not required | Application health status |
| `/actuator/info`   | GET    | ❌ Not required | Application information   |

### **3. Configuration**

- Environment-specific health details visibility
- Application metadata in info endpoint
- Integration with existing security

---

## 📊 Endpoint Examples

### **1. Health Check**

**Request:**

```bash
curl http://localhost:9000/api/actuator/health
```

**Response:**

```json
{
  "status": "UP"
}
```

**With details (when authenticated):**

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 85735137280,
        "threshold": 10485760,
        "path": "/app",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

---

### **2. Application Info**

**Request:**

```bash
curl http://localhost:9000/api/actuator/info
```

**Response:**

```json
{
  "app": {
    "name": "Meal Tracker API",
    "description": "REST API for tracking meals and calorie consumption",
    "version": "0.0.1-SNAPSHOT"
  },
  "java": {
    "version": "21.0.9",
    "vendor": {
      "name": "Amazon.com Inc.",
      "version": "Corretto-21.0.9.11.1"
    },
    "runtime": {
      "name": "OpenJDK Runtime Environment",
      "version": "21.0.9+11-LTS"
    },
    "jvm": {
      "name": "OpenJDK 64-Bit Server VM",
      "vendor": "Amazon.com Inc.",
      "version": "21.0.9+11-LTS"
    }
  },
  "os": {
    "name": "Mac OS X",
    "version": "15.6.1",
    "arch": "aarch64"
  }
}
```

---

## 🔧 Configuration Details

### **application.yml**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      show-components: when-authorized
  info:
    env:
      enabled: true
    java:
      enabled: true
    os:
      enabled: true

info:
  app:
    name: Meal Tracker API
    description: REST API for tracking meals and calorie consumption
    version: '@project.version@'
```

### **Security Configuration**

```java
.authorizeHttpRequests(authorize ->authorize
        .

requestMatchers("/actuator/health/**").

permitAll()
    .

requestMatchers("/actuator/info").

permitAll()
// Other endpoints require authentication
    .

anyRequest().

authenticated())
```

---

## 🐳 Docker Health Check

The Dockerfile already includes a health check using the actuator endpoint:

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9000/api/actuator/health || exit 1
```

**Benefits:**

- ✅ Docker/Kubernetes can monitor container health
- ✅ Automatic restart on health check failures
- ✅ Load balancers can route traffic based on health
- ✅ Orchestration platforms can manage deployments

---

## 🎯 Use Cases

### **1. Kubernetes/Docker Liveness & Readiness Probes**

```yaml
# kubernetes deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mealtracker-api
spec:
  template:
    spec:
      containers:
        - name: api
          image: tmhung/mealtracker:latest
          livenessProbe:
            httpGet:
              path: /api/actuator/health
              port: 9000
            initialDelaySeconds: 30
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /api/actuator/health
              port: 9000
            initialDelaySeconds: 10
            periodSeconds: 5
```

### **2. Load Balancer Health Checks**

**AWS Application Load Balancer:**

- Health check path: `/api/actuator/health`
- Expected response: `200 OK`
- Health check interval: 30 seconds

**Nginx Upstream Health Check:**

```nginx
upstream mealtracker {
    server localhost:9000 max_fails=3 fail_timeout=30s;
    
    # Health check
    check interval=3000 rise=2 fall=5 timeout=1000 type=http;
    check_http_send "GET /api/actuator/health HTTP/1.0\r\n\r\n";
    check_http_expect_alive http_2xx;
}
```

### **3. Monitoring & Alerting**

**Prometheus/Grafana:**

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'mealtracker-api'
    metrics_path: '/api/actuator/health'
    static_configs:
      - targets: [ 'localhost:9000' ]
```

**Simple Uptime Monitor:**

```bash
#!/bin/bash
# Check health every minute
while true; do
    status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9000/api/actuator/health)
    if [ "$status" != "200" ]; then
        echo "Alert: API is DOWN (status: $status)"
        # Send notification (email, Slack, etc.)
    fi
    sleep 60
done
```

---

## 🔐 Security Considerations

### **Current Setup (Public Health Check)**

✅ **Health endpoint is public** - Good for:

- Load balancers
- Container orchestration
- External monitoring services
- DevOps automation

⚠️ **Limited information** - Anonymous users see only:

```json
{
  "status": "UP"
}
```

### **Authenticated Access (More Details)**

✅ **When authenticated** - Users see:

- Database status
- Disk space
- Individual component health
- Detailed metrics

### **To Restrict Health Endpoint (Optional)**

If you want to require authentication for health checks:

```java
// Remove these lines from WebSecurityConfig
.requestMatchers("/actuator/health/**").

permitAll()
.

requestMatchers("/actuator/info").

permitAll()

// Add authentication requirement
.

requestMatchers("/actuator/**").

hasRole("ADMIN")
```

Then update Docker health check to use a health check user:

```dockerfile
HEALTHCHECK CMD wget --header="Authorization: Bearer ${HEALTH_CHECK_TOKEN}" \
  http://localhost:9000/api/actuator/health || exit 1
```

---

## 📈 Additional Actuator Endpoints (Optional)

You can expose more endpoints by updating `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,loggers
```

### **Useful Endpoints:**

| Endpoint               | Description                  | Security Recommendation |
|------------------------|------------------------------|-------------------------|
| `/actuator/metrics`    | Application metrics          | Require authentication  |
| `/actuator/env`        | Environment properties       | Require ADMIN role      |
| `/actuator/loggers`    | Change log levels at runtime | Require ADMIN role      |
| `/actuator/threaddump` | Thread dump                  | Require ADMIN role      |
| `/actuator/heapdump`   | Heap dump                    | Require ADMIN role      |

**Example to expose metrics:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

# In WebSecurityConfig.java
  .requestMatchers("/actuator/metrics/**").hasRole("ADMIN")
```

---

## ✅ Verification

### **Tests Added**

- ✅ `ActuatorHealthIT` - Integration tests for health endpoints
- ✅ Tests verify public access (no authentication)
- ✅ Tests verify correct JSON responses

### **Test Results**

```
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
✅ health_NoAuthentication_ReturnsUp
✅ info_NoAuthentication_ReturnsAppInfo
```

---

## 🚀 Quick Test

```bash
# Start the application
./mvnw spring-boot:run

# Test health endpoint
curl http://localhost:9000/api/actuator/health

# Test info endpoint
curl http://localhost:9000/api/actuator/info

# Test with pretty print
curl -s http://localhost:9000/api/actuator/health | jq .
```

---

## 📊 Files Modified

1. **pom.xml** - Added `spring-boot-starter-actuator` dependency
2. **application.yml** - Configured actuator endpoints and info
3. **WebSecurityConfig.java** - Made health/info endpoints public
4. **ActuatorHealthIT.java** - Integration tests for health endpoints

---

## 🎉 Benefits

| Feature                       | Before          | After              |
|-------------------------------|-----------------|--------------------|
| **Health monitoring**         | ❌ None          | ✅ /actuator/health |
| **App information**           | ❌ None          | ✅ /actuator/info   |
| **Docker health check**       | ⚠️ Basic        | ✅ Uses actuator    |
| **K8s readiness**             | ❌ Not supported | ✅ Ready            |
| **Load balancer integration** | ❌ Manual        | ✅ Automatic        |
| **Monitoring ready**          | ❌ No            | ✅ Yes              |

---

## 🎯 Next Steps (Optional)

1. **Add Metrics Endpoint** - Export application metrics
2. **Integrate Prometheus** - For time-series metrics
3. **Add Custom Health Indicators** - Check external services
4. **Set up Alerts** - Monitor health status changes
5. **Add APM Integration** - Datadog, New Relic, etc.

---

Your application now has **production-ready health checks**! 🏥✨
