package com.jahxx

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.routing.*
import kotlinx.html.*
import org.kodein.di.*
import org.kodein.type.jvmType


fun main() {
    embeddedServer(Netty, 8080) {
        kodeinApplication { application ->
            application.install(DefaultHeaders)

            bindSingleton { Users.Repository() }
            bindSingleton { Users.Controller(it) }
        }
    }.start(true)
}

object Users {

    class Controller(override val di: DI) : KodeinController() {
        private val repository: Repository by instance()

        override fun Routing.registerRoutes() {
            get<Routes.User> {
                call.respondHtml {
                    body {
                        ul {
                            for (user in repository.list()) {
                                li { a(application.href(Routes.User(user.name))) { +user.name } }
                            }
                        }
                    }
                }
            }

            get<Routes.User> {
                call.respondHtml {
                    body {
                        h1 { +it.name }
                    }
                }
            }
        }
    }

    data class User(val name: String)

    class Repository {
        private val initialUsers = listOf(User("test"), User("demo"))
        private val usersByName = LinkedHashMap<String, User>(initialUsers.associateBy { it.name })

        fun list() = usersByName.values.toList()
    }

    object Routes {
        @Resource("/users")
        object Users

        @Resource("/users/{name}")
        data class User(val name: String)
    }
}

fun Application.kodeinApplication(
    kodeinMapper: DI.MainBuilder.(Application) -> Unit = {}
) {
    val application = this

    application.install(Resources)
    application.install(DefaultHeaders)

    val kodein = DI {
        bind<Application>() with instance(application)
        kodeinMapper(this, application)
    }

    routing {
        for (bind in kodein.container.tree.bindings) {
            val bindClass = bind.key.type.jvmType as? Class<*>?
            if (bindClass != null && KodeinController::class.java.isAssignableFrom(bindClass)) {
                val res by kodein.Instance(bind.key.type)
                println("Registering '$res' routes...")
                (res as KodeinController).apply { registerRoutes() }
            }
        }
    }
}

abstract class KodeinController : DIAware {
    val application: Application by instance()

    abstract fun Routing.registerRoutes()
}

inline fun <reified T : Any> DI.MainBuilder.bindSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with singleton { callback(this@singleton.di) }
}
