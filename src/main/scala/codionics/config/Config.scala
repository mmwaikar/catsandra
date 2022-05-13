package codionics.config

case class Config(
    host: String,
    port: Int,
    datacenter: String,
    username: String,
    password: String,
    keyspace: String
)
