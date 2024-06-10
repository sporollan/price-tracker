db.createUser(
        {
            user: "user1",
            pwd: "mysecretpassword",
            roles: [
                {
                    role: "readWrite",
                    db: "products"
                }
            ]
        }
);