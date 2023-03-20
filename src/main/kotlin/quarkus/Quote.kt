package quarkus

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
class Quote {
    var id: String? = null
    var price = 0

    /**
     * Default constructor required for Jackson serializer
     */
    constructor()
    constructor(id: String?, price: Int) {
        this.id = id
        this.price = price
    }

    override fun toString(): String {
        return "Quote{" +
                "id='" + id + '\'' +
                ", price=" + price +
                '}'
    }
}