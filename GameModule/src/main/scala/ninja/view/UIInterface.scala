package ninja.view

import ninja.util.Observer

trait UIInterface extends Observer {

        def update: Unit


}
