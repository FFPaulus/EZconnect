# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

require_relative 'remoteDesktop'     # loads advice.rb
require_relative 'frameworkHelper'
require 'json'
require_relative 'login'
require 'abstract_controller/helpers'
require 'rack/websocket'

class App < Rack::WebSocket::Application

  def call(env)
    # request.params
    case env['REQUEST_PATH']
    # Homepage is Login page
    when '/'
      FrameworkHelper.make_login env

    #log out
    when '/logout'
      FrameworkHelper.make_logout env

    # Login successful, show remote desktop
    when '/remoteDesktop'
      FrameworkHelper.make_remote_desktop env

    # unknown url
    else
      FrameworkHelper.make_404

    end
  end
end

