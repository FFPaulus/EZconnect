# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

require 'yaml'
require 'ostruct'
require 'digest'
require 'pathname'
require 'active_support/core_ext/class/attribute_accessors'

class User


  attr_reader :session_id, :user_yml
  mattr_reader :user_obj

  def delete
    @session_id = nil
  end

  def initialize
    # get user credentials from file
    yml_path_storage = File.join(Dir.pwd, 'storage')
    yml_path = File.join(yml_path_storage, 'user.yml')
    yml_file = OpenStruct.new(YAML.load_file(yml_path))
    @user_yml = yml_file.username.to_s
    @passwd_yml = yml_file.password.to_s
    @@user_obj = self
  end

  def authenticate(params)
    # compare user credentials
    user = params["user"]
    passwd = Digest::SHA2.hexdigest params["passwd"]

    @session_id = SecureRandom.hex if user == @user_yml and passwd == @passwd_yml
    @session_id
  end

end

