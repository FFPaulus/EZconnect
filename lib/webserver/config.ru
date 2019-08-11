# config.ru

# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

require_relative 'app'
require 'rack'
require 'rack/websocket'
require 'rack/server'
require 'thin'
require 'yaml'
require 'ostruct'
require 'pathname'


app =
    Rack::Builder.new do |builder|
      # support noVNC JS library
      builder.use Rack::Static, :urls => ["/app", "/vendor", "/core"], :root => "noVNC"

      # use session cookies
      builder.use Rack::Session::Cookie, :key => 'rack.session',
          :path => '/',
          :expire_after => 2592000,
          :secret => SecureRandom.hex

      builder.run App.new
    end

# read port from config -> also done in remoteDesktop.erb
path_lib = File.join(Dir.pwd, 'lib')
path_webserver = File.join(path_lib, 'webserver')
settings_yml_path = File.join(path_webserver, 'settings.yml')
settings_yml_file = OpenStruct.new(YAML.load_file(settings_yml_path))
port_yml = settings_yml_file['port_website']
Dir.chdir(path_webserver) do
  # start webserver
  Thin::Server.start('0.0.0.0', port_yml, app)
end

