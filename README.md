# TextBin (Spring Boot)

---

# Setup

# Docker

```sh
docker build -t textbin-sb:build .
```

```sh
docker run --rm -p 8000:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://172.17.0.2:3306/TextBin -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=password -e GRC_SECRET=YOUR_KEY textbin-sb:build
```