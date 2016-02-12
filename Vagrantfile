$domain_name = "IoT-ConnectedCar"
$vagrant_ip = "33.33.164.176"
$box_name = "IoT-ConnectedCar.box"
$box_path = "http://devops.cloudspace.com/images/iot-connected/"
$cpus = 4
$memory = 4096

Vagrant.configure(2) do |config|
  org = $domain_name
  config.vm.box = $box_name
  config.vm.box_url = File.join($box_path, $box_name)
  config.ssh.forward_agent = true
  config.ssh.username = "vagrant"
  config.ssh.password = "vagrant"
  config.vm.network "private_network", ip: $vagrant_ip
  config.vm.network "public_network", bridge: "en0: Wi-Fi (AirPort)"
  config.vm.synced_folder "./", "/opt/pivotal/#{org}", :nfs => { :mount_options => ["dmode=777","fmode=777"] }

  config.vm.provider "virtualbox" do |v|
    v.customize ["modifyvm", :id, "--memory", $memory, "--name", $domain_name,"--cpus", $cpus]
  end
end