# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

require_relative 'user'
require 'action_dispatch/middleware/flash'
require 'action_controller'

class Login
  def self.new
    # No need for anything in here, we are just going to render our login page
  end

  def self.create credentials, env
    user = User.new

    # method to see if the password submitted on the login form was correct:
    auth = user.authenticate(credentials)
    if user && auth
      # Save the user.id in that user's session cookie:

      request = Rack::Request.new(env)
      request.session[:user_id]=auth

      return true
    else
      # if user or password incorrect, re-render login page:
      return false
    end
  end



  def self.destroy env
    env['rack.session.options'][:expire_after] = 0
  end
end
