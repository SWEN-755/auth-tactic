# Authentication and Authorization Tactics

## System Overview

This application is a minimal prototype demonstrating the Authentication and Authorization security tactics using the Java Authentication and Authorization Service (JAAS). 

### Tactic Design

The system implements two main security tactics:

1. **Authentication**: The Authentication tactic verifies the identity of the user attempting to access the system. This is primarily handled by the LoginAuthentication module (which implements the JAAS LoginModule interface). It checks the username and password provided by the user against credentials. A successful check confirms the user is who they claim to be.

2. **Authorization**: The Authorization tactic determines what an authenticated user is permitted to do based on their assigned role. The Authorization tactic is established across two distinct phases. Curing the login's commit phase in LoginAuthentication, role hierarchy is enforced. Then in Main, the system maintains a Policy Map linking service URLs to their single required role. The final authorization check is performed by the isAuthorized method, which checks if the authenticated user's Subject (the set of roles granted during login) contains the exact role specified by the service's URL.

## Running the App Locally

1. **Step 1:** Compile the files in directory root
```
javac *.java
```

2. **Step 2:** Execute the main file
```
java Main
```

## Libraries and Frameworks Used

**`java.util`** - Used for data structures such as HashMap, HashSet, Map, and Set.

**`java.security`** - Provides authentication and login capabilities, configuration with the JAAS Framework, and interface for a Principal entity which can be authenticated by a computer or network.

**`JAAS Framework`** - Provides a plug-and-play implementation for authentication and authorization.