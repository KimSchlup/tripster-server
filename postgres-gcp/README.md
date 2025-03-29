# 🗺️ MapMates PostgreSQL + PostGIS Setup on Google Cloud Free Tier

This guide walks you through setting up a PostgreSQL + PostGIS database using Docker on a [free-tier Google Cloud VM](https://cloud.google.com/free/docs/free-cloud-features#compute).

---

## ☁️ 1. Enable Compute Engine API

Activate the Compute Engine API for your project:  
👉 [Enable API](https://console.cloud.google.com/marketplace/product/google/compute.googleapis.com?returnUrl=%2Fnetworking%2Fnetworks%2Flist%3Freferrer%3Dsearch%26hl%3Dde%26inv%3D1%26invt%3DAbtUzA%26project%3Dsopra-fs25-group-08-server&hl=de&inv=1&invt=AbtUzA&project=sopra-fs25-group-08-server)

---

## 🔒 2. Configure Firewall Rule

Go to **VPC network > Firewall** and create a new rule:

-   **Name**: `allow-postgres`
-   **Description**: `Allow postgres on port 5432`
-   **Target Tags**: `allow-tcp-5432`
-   **Source IPv4 Ranges**: `0.0.0.0/0`
-   **Protocols and Ports**:
    -   Check `Specified protocols and ports`
    -   TCP: `5432`

---

## 💻 3. Create a VM (Free Tier Eligible)

Now we can create a VM, be careful to adhere to the free limits.
Navigate to **Compute Engine > VM Instances**, then:

In Machine configuration:

-   **Region**: `us-central1`
-   **Zone**: `us-central1-a`
-   **Machine type**: `e2-micro`

In OS and storage: Click `change`

-   **Boot disk**: Standard Persistent Disk (max 30 GB)

In Networking add the Network Tag previously set up in our firewall rule:

-   **Network Tags**: `allow-tcp-5432`

---

## 🐧 4. Install Docker on the VM

No on our VM instances dashboard connect via SSH into the VM, then install Docker using the official [Docker Debian instructions](https://docs.docker.com/engine/install/debian/):

```bash
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

```

```bash
# To install the latest version, run:
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

```

```bash
# Test
 sudo docker run hello-world
```

---

## 🐘 5. Run PostgreSQL + PostGIS Docker Container

(postgis/postgis` Docker image **already includes PostgreSQL**.)

```bash
sudo docker pull postgis/postgis

sudo docker run --name mapmates-postgres \
  -e POSTGRES_PASSWORD=mapmates123 \
  -v postgres:/var/lib/postgresql/data \
  -p 5432:5432 \
  -d postgis/postgis
```

---

## 🛠️ 6. Set Up the Database

Enter the container and set up PostGIS:

```bash
sudo docker exec -it mapmates-postgres psql -U postgres

-- Inside the psql shell:

CREATE DATABASE mapmates;
\c mapmates
CREATE EXTENSION postgis;
```

---

## 🧠 7. Access via pgAdmin (Optional)

Use **pgAdmin** or another PostgreSQL client to connect:

-   **General Tab**

    -   Name: `GCP Postgres`

-   **Connection Tab**
    -   **Host**: _Your VM’s External IP_
    -   **Port**: `5432`
    -   **Username**: `postgres`
    -   **Password**: `mapmates123`

---

✅ You now have a running PostgreSQL + PostGIS instance on GCP using Docker!
