# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

require 'erb'
class FrameworkHelper
  def self.erb(filename, local = {})
    b = binding
    message = local[:message]
    content = File.read("views/#{filename}.erb")
    ERB.new(content).result(b)
  end

  def self.response(status, headers, body = '')
    body = yield if block_given?
    [status, headers, [body]]
  end

  #called to make login page
  def self.make_login env
    req = Rack::Request.new(env)
    status = '200'
    headers = {"Content-Type" => 'text/html'}
    Login.new
    FrameworkHelper.response(status, headers) do
      #redirect to remoteDesktop if still logged in to prevent logging in twice
      if req.session[:user_id]
        FrameworkHelper.erb :"remoteDesktop"
      else
        Login.new
        FrameworkHelper.erb :'index'
      end
    end
  end

  #called to log out and make logout page
  def self.make_logout env
    status = '200'
    headers = {"Content-Type" => 'text/html'}
    Login.destroy env
    FrameworkHelper.response(status, headers) do
      FrameworkHelper.erb :index
    end
  end

  #called to make remote desktop page and to log in
  def self.make_remote_desktop env
    req = Rack::Request.new(env)
    #try to log in
    if req.post?
      credentials = JSON.parse('{"' + CGI.unescape(req.body.read).gsub('=', '":"').gsub('&', '","') + '"}')
      Login.create credentials, env
    end

    #show remote desktop page if successfully logged in
    status = '200'
    headers = {"Content-Type" => 'text/html'}
    FrameworkHelper.response(status, headers) do
      if req.session[:user_id]
        FrameworkHelper.erb :"remoteDesktop"
      else
        FrameworkHelper.erb :'index', message: "Login failed. Please try again!"
      end
    end
  end

  def self.make_404
    [
        '404',
        {"Content-Type" => 'text/html', "Content-Length" => '48'},
        ["<html><body><h4>404 Not Found</h4></body></html>"]
    ]
  end
end
