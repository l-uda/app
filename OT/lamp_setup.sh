#https://magefan.com/blog/install-local-lamp-server-for-ubuntu

 sudo apt update
 sudo apt upgrade
 
 sudo apt install apache2
 sudo ufw app list
 sudo ufw allow 'Apache Full'
 sudo ufw default allow
 sudo iptables -L
 sudo ufw enable
 sudo ufw status
 sudo systemctl status apache2
 
 sudo apt install mysql-server
 sudo service mysql status
 sudo mysql_secure_installation
 
 
CREATE USER 'phpmyadmin'@'localhost' IDENTIFIED BY '<New-Password-Here>';
GRANT ALL PRIVILEGES ON *.* TO 'phpmyadmin'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
exit
 
 sudo apt install php libapache2-mod-php php7.4-mysql php7.4-common php7.4-mysql php7.4-xml php7.4-xmlrpc php7.4-curl php7.4-gd php7.4-imagick php7.4-cli php7.4-dev php7.4-imap php7.4-mbstring php7.4-opcache php7.4-soap php7.4-zip php7.4-bcmath php7.4-mysqli php7.4-intl -y
 
 php -v
 
 sudo gedit /etc/php/7.4/apache2/php.ini
 sudo a2dissite 000-default
 sudo mkdir -p /var/www/html/luda/public
 sudo chmod -R 755 /var/www/html/luda
 sudo chown -R www-data:www-data /var/www/html/luda
 sudo gedit /etc/apache2/sites-available/luda.conf
 sudo a2ensite luda.conf
 systemctl reload apache2
 
 sudo gedit /var/www/html/luda/public/info.php


